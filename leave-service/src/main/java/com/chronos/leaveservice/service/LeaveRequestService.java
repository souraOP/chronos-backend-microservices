package com.chronos.leaveservice.service;

import com.chronos.leaveservice.dto.leaveRequests.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LeaveRequestService {
    @Transactional
    LeaveRequestResponseDTO createLeaveRequest(String employeeId, LeaveRequestCreateRequestDTO request);

    void actionOnLeaveRequest(String managerId, String requestId, LeaveRequestActionDTO leaveRequestActionDTO);

    List<LeaveRequestResponseDTO> getEmployeeLeaveRequests(String employeeId);

    List<ManagerLeaveRequestDTO> getTeamLeaveRequests(String managerId);

    ManagerLeaveRequestDataDTO getLeaveRequestsStatsByManager(String managerId);

    List<ManagerLeaveRequestDashboardResponseDTO> getLeaveRequestManagerDashboard(String managerId);

    List<EmployeeLeaveRequestDashboardResponseDTO> getLeaveRequestEmployeeDashboard(String employeeId);
}
