package com.chronos.shiftservice.dto.shiftSwapRequest;


import com.chronos.common.constants.enums.ShiftSwapRequestStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ShiftSwapQueryResponseDTO(
        UUID id,
        String shiftSwapId,
        String fromEmployeeName,
        String toEmployeeName,
        ShiftSwapRequestStatus status,

        // offeringShift
        String offeringShiftType,
        LocalDate offeringShiftDate,
        OffsetDateTime offeringShiftStartTime,
        OffsetDateTime offeringShiftEndTime,
        String offeringShiftLocation,

        // requestingShift
        String requestingShiftType,
        LocalDate requestingShiftDate,
        OffsetDateTime requestingShiftStartTime,
        OffsetDateTime requestingShiftEndTime,
        String requestingShiftLocation,

        String reason,
        String approvedByName,
        OffsetDateTime approvedDate
) {
}
