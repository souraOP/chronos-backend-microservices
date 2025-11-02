package com.chronos.leaveservice.service.impl;

import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.UuidErrorConstants;
import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.EmployeeNotFoundException;
import com.chronos.common.exception.custom.InvalidLeaveRequestException;
import com.chronos.common.exception.custom.LeaveBalanceNotFoundException;
import com.chronos.common.exception.custom.LeaveRequestNotFoundException;
import com.chronos.leaveservice.dto.leaveRequests.*;
import com.chronos.leaveservice.entity.LeaveBalance;
import com.chronos.leaveservice.entity.LeaveRequest;
import com.chronos.leaveservice.feign.EmployeeClient;
import com.chronos.leaveservice.repository.LeaveBalanceRepository;
import com.chronos.leaveservice.repository.LeaveRequestRepository;
import com.chronos.leaveservice.util.mapper.LeaveRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static com.chronos.common.util.ParseUUID.parseUUID;
import static com.chronos.leaveservice.util.CalculateLeaveRequestDays.getLeaveRequestDays;


@Service
public class LeaveRequestServiceImpl implements com.chronos.leaveservice.service.LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeClient employeeClient;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeClient employeeClient,
            LeaveBalanceRepository leaveBalanceRepository
    ) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeClient = employeeClient;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    @Transactional
    @Override
    public LeaveRequestResponseDTO createLeaveRequest(String employeeId, LeaveRequestCreateRequestDTO request) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        EmployeeDTO employee = employeeClient.getEmployeeById(employeeId);

        if(employee == null) {
            throw new EmployeeNotFoundException(ErrorConstants.EMPLOYEE_NOT_FOUND);
        }

        if (request.startDate().isAfter(request.endDate())) {
            throw new InvalidLeaveRequestException(ErrorConstants.INVALID_LEAVE_REQUESTS);
        }

        LeaveBalance lb = leaveBalanceRepository.findByEmployeeIdAndLeaveType(empID, request.leaveType())
                .orElseThrow(() -> new LeaveBalanceNotFoundException(ErrorConstants.LEAVE_BALANCE_NOT_FOUND));


        LeaveRequestCreateDTO leaveRequestCreateDTO = new LeaveRequestCreateDTO(
                request.leaveType(),
                request.startDate(),
                request.endDate(),
                request.reason()
        );


        LeaveRequest leaveRequestEntity = LeaveRequestMapper.leaveRequestDtoToEntity(leaveRequestCreateDTO, empID);

        int days = getLeaveRequestDays(leaveRequestEntity);
        if(lb.getLeaveBalance() < days) {
            throw new InvalidLeaveRequestException(ErrorConstants.INSUFFICIENT_LEAVE_BALANCE);
        }


        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequestEntity);
        return LeaveRequestMapper.leaveRequestEntityToResponse(savedLeaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDTO> getEmployeeLeaveRequests(String employeeId) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findLeaveRequestsByEmployeeId(empID);

        List<LeaveRequestResponseDTO> response = new ArrayList<>();

        for (LeaveRequest lr : leaveRequests) {
            LeaveRequestResponseDTO singleLeaveRequestDto = LeaveRequestMapper.leaveRequestEntityToResponse(lr);
            response.add(singleLeaveRequestDto);
        }

        return response;
    }

    @Override
    public List<ManagerLeaveRequestDTO> getTeamLeaveRequests(String managerId) {
        List<EmployeeDTO> teamMembers = employeeClient.getTeamMembers(managerId);

        if(teamMembers == null || teamMembers.isEmpty()) {
            return List.of();
        }

        Map<UUID, EmployeeDTO> byId = new HashMap<>();

        List<UUID> empIds = new ArrayList<>();
        for(EmployeeDTO e: teamMembers) {
            if(e.id() != null) {
                empIds.add(e.id());
                byId.put(e.id(), e);
            }
        }

        List<LeaveRequest> teamsLeaveRequests = leaveRequestRepository.findByEmployeeIdInOrderByRequestDateDesc(empIds);

        List<ManagerLeaveRequestDTO> result = new ArrayList<>();
        for(LeaveRequest lr: teamsLeaveRequests) {
            EmployeeDTO emp = byId.get(lr.getEmployeeId());
            if(emp != null) {
                result.add(LeaveRequestMapper.leaveRequestManagerEntityToDto(lr, emp));
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void actionOnLeaveRequest(String managerId, String requestId, LeaveRequestActionDTO leaveRequestActionDTO) {
        UUID leaveRequestID = parseUUID(requestId, UuidErrorConstants.INVALID_LEAVE_REQUEST_ID);

        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestID)
                .orElseThrow(() -> new LeaveRequestNotFoundException(ErrorConstants.LEAVE_REQUEST_NOT_FOUND));

        if (lr.getLeaveStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException(ErrorConstants.LEAVE_REQUEST_ALREADY_PROCESSED);
        }

        List<EmployeeDTO> teamMembers = employeeClient.getTeamMembers(managerId);
        Set<UUID> managerIds = new HashSet<>();
        for(EmployeeDTO e: teamMembers) {
           if(e.id() != null) {
               managerIds.add(e.id());
           }
        }

        if(!managerIds.contains(lr.getEmployeeId())) {
            throw new InvalidLeaveRequestException(ErrorConstants.EMPLOYEE_NOT_IN_MANAGER_TEAM);
        }


        LeaveStatus action = leaveRequestActionDTO.action();

        if (action == LeaveStatus.APPROVED) {
            LeaveBalance lb = leaveBalanceRepository.findByEmployeeIdAndLeaveType(lr.getEmployeeId(), lr.getLeaveType())
                    .orElseThrow(() -> new LeaveBalanceNotFoundException(ErrorConstants.LEAVE_BALANCE_NOT_FOUND));


            int days = getLeaveRequestDays(lr);

            if (lb.getLeaveBalance() < days) {
                throw new InvalidLeaveRequestException(ErrorConstants.INSUFFICIENT_LEAVE_BALANCE);
            }


            int required = Math.toIntExact(days);
            int current = lb.getLeaveBalance();

            lb.setLeaveBalance(current - required);

            leaveBalanceRepository.save(lb);
            lr.setLeaveStatus(LeaveStatus.APPROVED);
        } else if (leaveRequestActionDTO.action() == LeaveStatus.REJECTED) {
            lr.setLeaveStatus(LeaveStatus.REJECTED);
        } else {
            throw new InvalidLeaveRequestException(ErrorConstants.INVALID_LEAVE_REQUESTS);
        }
        leaveRequestRepository.save(lr);
    }

    @Override
    public ManagerLeaveRequestDataDTO getLeaveRequestsStatsByManager(String managerId) {
        List<EmployeeDTO> team = employeeClient.getTeamMembers(managerId);

        if(team == null || team.isEmpty()){
            return new ManagerLeaveRequestDataDTO(0,0,0,0);
        }

        List<UUID> ids = team.stream().map(EmployeeDTO::id).filter(Objects::nonNull).toList();

        LocalDate now = LocalDate.now();

        long pending = leaveRequestRepository.countByEmployeeIdInAndLeaveStatus(ids, LeaveStatus.PENDING);
        long approved = leaveRequestRepository.countByEmployeeIdInAndLeaveStatus(ids, LeaveStatus.APPROVED);
        long rejected = leaveRequestRepository.countByEmployeeIdInAndLeaveStatus(ids, LeaveStatus.REJECTED);
        long onLeaveToday = leaveRequestRepository.countByOnLeaveToday(ids, now);

        return new ManagerLeaveRequestDataDTO(pending, approved, rejected, onLeaveToday);
    }

    @Override
    public List<ManagerLeaveRequestDashboardResponseDTO> getLeaveRequestManagerDashboard(String managerId) {
        List<EmployeeDTO> team = employeeClient.getTeamMembers(managerId);

        if(team == null || team.isEmpty()) {
            return List.of();
        }

        Map<UUID, String> nameById = new HashMap<>();
        List<UUID> ids = new ArrayList<>();

        for(EmployeeDTO e: team) {
            if(e.id() != null) {
                ids.add(e.id());
                String name = e.firstName() + (e.lastName() == null || e.lastName().isBlank() ? "" : " " + e.lastName());
                nameById.put(e.id(), name);
            }
        }

        return leaveRequestRepository.findByEmployeeIdInOrderByRequestDateDesc(ids).stream()
                .map(lr -> new ManagerLeaveRequestDashboardResponseDTO(
                        lr.getLeaveRequestId(),
                        nameById.getOrDefault(lr.getEmployeeId(), ""),
                        lr.getLeaveType(),
                        lr.getStartDate(),
                        lr.getEndDate()
                )).toList();
    }

    @Override
    public List<EmployeeLeaveRequestDashboardResponseDTO> getLeaveRequestEmployeeDashboard(String employeeId) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);
        return leaveRequestRepository.leaveRequestEmployeeDashboard(empID);
    }
}
