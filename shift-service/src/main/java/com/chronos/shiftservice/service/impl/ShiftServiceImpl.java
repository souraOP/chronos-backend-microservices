package com.chronos.shiftservice.service.impl;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.UuidErrorConstants;
import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.ResourceNotFoundException;
import com.chronos.common.exception.custom.ShiftNotFoundException;
import com.chronos.common.util.NanoIdGenerator;
import com.chronos.shiftservice.dto.shift.CreateNewShiftDTO;
import com.chronos.shiftservice.dto.shift.CreateShiftDateRequestDTO;
import com.chronos.shiftservice.dto.shift.ShiftResponseDTO;
import com.chronos.shiftservice.dto.shift.TeamShiftTableRowDTO;
import com.chronos.shiftservice.entity.Shift;
import com.chronos.shiftservice.feign.EmployeeClient;
import com.chronos.shiftservice.repository.ShiftRepository;
import com.chronos.shiftservice.service.ShiftService;
import com.chronos.shiftservice.utils.mappers.ShiftMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.chronos.common.util.ParseUUID.parseUUID;


@Slf4j
@Service
public class ShiftServiceImpl implements ShiftService {
    private final ShiftRepository shiftRepository;
    private final EmployeeClient employeeClient;

    @Autowired
    public ShiftServiceImpl(
            ShiftRepository shiftRepository,
            EmployeeClient employeeClient

    ) {
        this.shiftRepository = shiftRepository;
        this.employeeClient = employeeClient;
    }

    @Override
    @Transactional
    public ShiftResponseDTO createShift(CreateShiftDateRequestDTO shiftDTO, String managerId) {
        log.info("Invoked the createShift service method, shiftDTO:{}, managerId:{}", shiftDTO, managerId);
        UUID empID = shiftDTO.employeeId();

        List<EmployeeDTO> teamMembers = employeeClient.getTeamMembers(managerId);
        boolean belongs = teamMembers.stream().anyMatch(e -> empID.equals(e.id()));

        if (!belongs) {
            throw new ResourceNotFoundException(ErrorConstants.EMPLOYEE_NOT_IN_MANAGER_TEAM);
        }
        // have to convert it from iso8601 date
        // to offsetdatetime
        ZoneId zone = ZoneId.systemDefault();

        LocalDate shiftDate = shiftDTO.shiftDate().atStartOfDay().toLocalDate();
        OffsetDateTime shiftStart = shiftDTO.shiftDate().atTime(shiftDTO.shiftStartTime()).atZone(zone).toOffsetDateTime();
        OffsetDateTime shiftEnd = shiftDTO.shiftDate().atTime(shiftDTO.shiftEndTime()).atZone(zone).toOffsetDateTime();

        if (shiftEnd.isBefore(shiftStart)) {
            throw new IllegalArgumentException(ErrorConstants.INVALID_SHIFT_TIMING);
        }

        CreateNewShiftDTO offsetShiftDTO = new CreateNewShiftDTO(
                shiftDTO.employeeId(),
                shiftDate,
                shiftStart,
                shiftEnd,
                ShiftStatus.CONFIRMED,
                shiftDTO.shiftType(),
                shiftDTO.shiftLocation()
        );

        Shift shift = new Shift();

        int shiftIdLength = 10;
        String nanoId = NanoIdUtils.randomNanoId(
                NanoIdGenerator.DEFAULT_NUMBER_GENERATOR,
                NanoIdGenerator.DEFAULT_ALPHABET,
                shiftIdLength
        );

        shift.setPublicId("SH-" + nanoId);
        shift.setEmployeeId(offsetShiftDTO.employeeId());
        shift.setShiftDate(offsetShiftDTO.shiftDate());
        shift.setShiftStartTime(offsetShiftDTO.shiftStartTime());
        shift.setShiftEndTime(offsetShiftDTO.shiftEndTime());
        shift.setShiftType(offsetShiftDTO.shiftType());
        shift.setShiftStatus(offsetShiftDTO.shiftStatus());
        shift.setShiftLocation(offsetShiftDTO.shiftLocation());

        Shift savedShift = shiftRepository.save(shift);

        log.info("Created new shift with publicId:{}", savedShift.getPublicId());

        return ShiftMapper.shiftEntityToDto(savedShift);
    }

    @Override
    public List<ShiftResponseDTO> getEmployeeShifts(String employeeId) {
        log.info("Invoked the getEmployeeShifts service method, employeeId:{}", employeeId);
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        List<Shift> shifts = shiftRepository.findShiftByEmployeeIdAndDateAsc(empID)
                .orElseThrow(() -> new ShiftNotFoundException(ErrorConstants.SHIFT_NOT_FOUND));

        log.info("Returning {} shifts for employeeId:{}", shifts.size(), empID);
        return shifts.stream().map(s -> new ShiftResponseDTO(
                s.getId(),
                s.getPublicId(),
                s.getShiftDate(),
                s.getShiftStartTime(),
                s.getShiftEndTime(),
                s.getShiftStatus(),
                s.getShiftType(),
                s.getShiftLocation()
        )).toList();
    }

    @Override
    @CircuitBreaker(name="employee-service", fallbackMethod="getDefaultTeamShiftByManager")
    public List<ShiftResponseDTO> getTeamsShiftByManager(String managerId) {
        log.info("Invoked the getTeamsShiftByManager service method, managerId:{}", managerId);
        List<EmployeeDTO> team = employeeClient.getTeamMembers(managerId);

        if (team.isEmpty()) {
            return List.of();
        }

        List<UUID> empIds = team.stream().map(EmployeeDTO::id).toList();

        List<Shift> shifts = shiftRepository.findMultipleShiftByEmployeeId(empIds);

        return shifts.stream()
                .map(shift -> ShiftMapper.shiftEntityToDto(shift))
                .toList();

    }

    @Override
    public List<TeamShiftTableRowDTO> getTeamShiftsByManagerAndDatePicker(String managerId, LocalDate date) {
        log.info("Invoked the getTeamShiftsByManagerAndDatePicker service method, managerId:{}, date:{}", managerId, date);
        List<EmployeeDTO> team = employeeClient.getTeamMembers(managerId);
        if (team.isEmpty()) {
            throw new ResourceNotFoundException(ErrorConstants.MANAGER_WITH_NO_TEAM);
        }

        Map<UUID, String> idToName = team.stream().collect(Collectors.toMap(
                EmployeeDTO::id,
                e -> {
                    String lName = e.lastName();
                    return lName == null || lName.isBlank() ? e.firstName() : e.firstName() + " " + lName;
                }
        ));

        List<UUID> empIds = new ArrayList<>(idToName.keySet());
        List<Shift> shifts = shiftRepository.findTeamShiftRowByEmployeeIdsAndDateBetween(empIds, date);


        return shifts.stream().map(s -> new TeamShiftTableRowDTO(
                s.getId(),
                s.getPublicId(),
                idToName.getOrDefault(s.getEmployeeId(), ""),
                s.getShiftDate(),
                s.getShiftStartTime(),
                s.getShiftEndTime(),
                s.getShiftType(),
                s.getShiftLocation(),
                s.getShiftStatus()
        )).toList();
    }


    public List<ShiftResponseDTO> getDefaultTeamShiftByManager(String managerId, Throwable t){
        log.error("Circuit Breaker triggered for Employee Client call. Reason: {}", t.getMessage());
        return Collections.emptyList();
    }
}
