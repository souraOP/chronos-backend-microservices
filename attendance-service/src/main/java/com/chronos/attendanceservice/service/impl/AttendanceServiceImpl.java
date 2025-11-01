package com.chronos.attendanceservice.service.impl;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.chronos.attendanceservice.dto.EmployeeDTO;
import com.chronos.attendanceservice.dto.attendance.AttendanceResponseDTO;
import com.chronos.attendanceservice.dto.attendance.CheckInRequestDTO;
import com.chronos.attendanceservice.dto.attendance.ManagerAttendanceDisplayByDateResponseDTO;
import com.chronos.attendanceservice.dto.attendance.ManagerAttendanceRowDTO;
import com.chronos.attendanceservice.entity.Attendance;
import com.chronos.attendanceservice.feign.EmployeeClient;
import com.chronos.attendanceservice.repository.AttendanceRepository;
import com.chronos.attendanceservice.service.AttendanceService;
import com.chronos.attendanceservice.util.mapper.AttendanceMapper;
import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.UuidErrorConstants;
import com.chronos.common.constants.enums.AttendanceStatus;
import com.chronos.common.exception.custom.ActiveAttendanceExistsException;
import com.chronos.common.exception.custom.ActiveAttendanceNotFoundException;
import com.chronos.common.exception.custom.EmployeeNotFoundException;
import com.chronos.common.exception.custom.InvalidDateException;
import com.chronos.common.util.NanoIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chronos.common.util.ParseUUID.parseUUID;


@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeClient employeeClient;

    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, EmployeeClient employeeClient) {
        this.attendanceRepository = attendanceRepository;
        this.employeeClient = employeeClient;
    }

    @Override
    public AttendanceResponseDTO getLatestAttendance(String employeeId) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);
        List<AttendanceResponseDTO> list = attendanceRepository.findAllByEmployeeOrderByDateDesc(empID);

        if (!list.isEmpty()) {
            return list.getFirst();
        }
        return new AttendanceResponseDTO(
                "N/A",
                LocalDate.now(),
                null,
                null,
                0.0,
                AttendanceStatus.COMPLETE,
                null
        );
    }

    @Override
    public List<AttendanceResponseDTO> getAttendanceHistory(String employeeId) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        return attendanceRepository.findAllByEmployeeOrderByDateDesc(empID);
    }

    @Override
    public AttendanceResponseDTO checkIn(String employeeId, CheckInRequestDTO checkInRequestDTO) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        EmployeeDTO employee = employeeClient.getEmployeeById(employeeId);

        if(employee == null) {
            throw new EmployeeNotFoundException(ErrorConstants.EMPLOYEE_NOT_FOUND);
        }

        List<Attendance> activeList = attendanceRepository.findLatestByEmployeeAndStatus(empID, AttendanceStatus.ACTIVE);

        if(!activeList.isEmpty()) {
            throw new ActiveAttendanceExistsException(ErrorConstants.ALREADY_CHECKED_IN);
        }

        OffsetDateTime now = OffsetDateTime.now();

        Attendance attendance = new Attendance();

        int attendanceIdLength = 10;
        String nanoId = NanoIdUtils.randomNanoId(
                NanoIdGenerator.DEFAULT_NUMBER_GENERATOR,
                NanoIdGenerator.DEFAULT_ALPHABET,
                attendanceIdLength
        );

        attendance.setAttendanceId("ATT-" + nanoId);
        attendance.setEmployeeId(empID);
        attendance.setDate(LocalDate.now());
        attendance.setCheckIn(now);
        attendance.setCheckOut(null);
        attendance.setHoursWorked(0.0);
        attendance.setAttendanceStatus(AttendanceStatus.ACTIVE);
        attendance.setLocation(checkInRequestDTO != null ? checkInRequestDTO.location() : null);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return AttendanceMapper.attendanceEntityToDto(savedAttendance);

    }

    @Override
    public AttendanceResponseDTO checkOut(String employeeId) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        EmployeeDTO employee = employeeClient.getEmployeeById(employeeId);

        if(employee == null) {
            throw new EmployeeNotFoundException(ErrorConstants.EMPLOYEE_NOT_FOUND);
        }

        List<Attendance> activeList = attendanceRepository.findLatestByEmployeeAndStatus(empID, AttendanceStatus.ACTIVE);
        Attendance attendance = activeList.isEmpty() ? null : activeList.get(0);

        if(attendance == null) {
            throw new ActiveAttendanceNotFoundException(ErrorConstants.ACTIVE_ATTENDANCE_NOT_FOUND);
        }

        OffsetDateTime now = OffsetDateTime.now();
        attendance.setCheckOut(now);

        double hours = 0.0;
        if(attendance.getCheckIn() != null) {
            hours = Duration.between(attendance.getCheckIn(), now).toMinutes() / 60.0;
        }

        attendance.setHoursWorked(hours);
        attendance.setAttendanceStatus(AttendanceStatus.COMPLETE);

        Attendance saved = attendanceRepository.save(attendance);
        return AttendanceMapper.attendanceEntityToDto(saved);
    }

    @Override
    public ManagerAttendanceDisplayByDateResponseDTO getTeamsAttendanceByDate(String managerId, String date) {
        UUID mngID = parseUUID(managerId, UuidErrorConstants.INVALID_MANAGER_UUID);

        if(employeeClient.getEmployeeById(managerId) == null) {
            throw new EmployeeNotFoundException(ErrorConstants.MANAGER_NOT_FOUND);
        }

        LocalDate localDate;

        try {
            localDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(ErrorConstants.INVALID_DATE_FORMAT);
        }

        List<EmployeeDTO> team = employeeClient.getTeamMembers(managerId);
        if(team == null || team.isEmpty()) {
            return new ManagerAttendanceDisplayByDateResponseDTO(localDate, List.of());
        }

        Map<UUID, EmployeeDTO> employeeById = team.stream()
                .filter(e -> e.id() != null)
                .collect(Collectors.toMap(EmployeeDTO::id, e -> e));

        List<UUID> ids = new ArrayList<>(employeeById.keySet());
        List<Attendance> rows = attendanceRepository.findTeamAttendanceByDate(ids, localDate);


        List<ManagerAttendanceRowDTO> result = rows.stream().map(
                a -> {
                    EmployeeDTO dto = employeeById.get(a.getEmployeeId());
                    String displayId = dto != null ? dto.displayEmployeeId() : "";
                    String name = dto != null ? (dto.firstName() + " " + (dto.lastName() == null ? "" : dto.lastName())).trim() : "";
                    return new ManagerAttendanceRowDTO(
                            displayId,
                            name,
                            a.getCheckIn(),
                            a.getCheckOut(),
                            a.getHoursWorked(),
                            a.getAttendanceStatus()
                    );
                }
        ).toList();

        return new ManagerAttendanceDisplayByDateResponseDTO(localDate, result);
    }
}
