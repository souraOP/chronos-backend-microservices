package com.chronos.leaveservice.dto.leaveRequests;

public record ManagerLeaveRequestDataDTO(
        long pending,
        long approved,
        long rejected,
        long onLeaveToday
) {
}
