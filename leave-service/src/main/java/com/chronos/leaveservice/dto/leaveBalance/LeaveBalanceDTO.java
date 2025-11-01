package com.chronos.leaveservice.dto.leaveBalance;

import com.chronos.common.constants.enums.LeaveType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record LeaveBalanceDTO(
        @NotNull(message = "Employee ID is required")
        UUID id,

        @NotBlank(message = "Valid balance ID is required")
        String balanceId,

        @NotNull(message = "Leave type is required")
        LeaveType leaveType,

        @Min(value = 0, message = "Leave Balance must be zero or positive")
        @PositiveOrZero
        int leaveBalance
) {
}
