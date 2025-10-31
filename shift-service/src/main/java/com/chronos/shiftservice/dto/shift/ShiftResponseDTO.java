package com.chronos.shiftservice.dto.shift;


import com.chronos.shiftservice.constants.enums.ShiftStatus;
import com.chronos.shiftservice.constants.enums.ShiftType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ShiftResponseDTO(
        UUID id,
        String shiftId,
        LocalDate shiftDate,
        OffsetDateTime shiftStartTime,
        OffsetDateTime shiftEndTime,
        ShiftStatus shiftStatus,
        ShiftType shiftType,
        String shiftLocation
) {
}
