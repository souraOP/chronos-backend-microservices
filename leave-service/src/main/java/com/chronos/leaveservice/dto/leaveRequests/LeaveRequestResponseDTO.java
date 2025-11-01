package com.chronos.leaveservice.dto.leaveRequests;

import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.constants.enums.LeaveType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@NotNull
public record LeaveRequestResponseDTO(
        String leaveRequestId,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        int days,
        LeaveStatus status,
        OffsetDateTime requestDate,
        String reason
) {
}
