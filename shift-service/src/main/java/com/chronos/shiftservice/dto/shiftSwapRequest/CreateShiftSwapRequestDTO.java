package com.chronos.shiftservice.dto.shiftSwapRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateShiftSwapRequestDTO(
        @NotNull(message = "Requester employee ID is required")
        UUID requesterEmployeeId,

        @NotNull(message = "Requested employee ID is required")
        UUID requestedEmployeeId,

        @NotNull(message = "Offering shift ID is required")
        UUID offeringShiftId,

        @NotNull(message = "Requesting Shift ID is required")
        UUID requestingShiftId,

        @NotBlank(message = "Reason is needed")
        String reason
) {
}
