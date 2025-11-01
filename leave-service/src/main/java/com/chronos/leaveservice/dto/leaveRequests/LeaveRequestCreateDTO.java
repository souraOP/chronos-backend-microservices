package com.chronos.leaveservice.dto.leaveRequests;

import com.chronos.common.constants.enums.LeaveType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LeaveRequestCreateDTO(
        @NotNull(message = "Leave Type is required")
        LeaveType leaveType,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotBlank
        @Size(min = 10, message = "Minimum length of the reason should be 10")
        String reason
) {
}
