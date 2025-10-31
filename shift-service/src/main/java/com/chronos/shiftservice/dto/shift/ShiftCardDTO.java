package com.chronos.shiftservice.dto.shift;


import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.constants.enums.ShiftType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ShiftCardDTO(
        UUID id,
        String shiftId,
        LocalDate shiftDate,
        OffsetDateTime shiftStartTime,
        OffsetDateTime shiftEndTime,
        String shiftLocation,
        ShiftType shiftType,
        ShiftStatus shiftStatus
) {
}
