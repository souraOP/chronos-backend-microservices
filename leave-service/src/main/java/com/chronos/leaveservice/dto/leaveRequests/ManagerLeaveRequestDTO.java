package com.chronos.leaveservice.dto.leaveRequests;


import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.constants.enums.LeaveType;

import java.time.LocalDate;
import java.util.UUID;

public record ManagerLeaveRequestDTO(
        UUID requestId,
        UUID employeeId,
        String displayEmployeeId,
        String employeeFirstName,
        String employeeLastName,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        int days,
        LeaveStatus status,
        String reason
) {
}
