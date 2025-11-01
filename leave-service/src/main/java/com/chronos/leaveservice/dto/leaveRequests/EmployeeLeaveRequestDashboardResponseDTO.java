package com.chronos.leaveservice.dto.leaveRequests;


import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.constants.enums.LeaveType;

import java.time.LocalDate;

public record EmployeeLeaveRequestDashboardResponseDTO(
        String leaveRequestId,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        LeaveStatus status
) {
}
