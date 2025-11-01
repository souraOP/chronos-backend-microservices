package com.chronos.leaveservice.dto.leaveRequests;


import com.chronos.common.constants.enums.LeaveType;

import java.time.LocalDate;

public record ManagerLeaveRequestDashboardResponseDTO(
        String leaveRequestId,
        String employeeName,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate
) {
}
