package com.chronos.shiftservice.dto.shift;

import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.constants.enums.ShiftType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TeamShiftTableRowDTO(
        UUID id,
        String shiftId,
        String employeeName,
        LocalDate shiftDate,
        OffsetDateTime shiftStartTime,
        OffsetDateTime shiftEndTime,
        ShiftType shiftType,
        String shiftLocation,
        ShiftStatus shiftStatus
) {
}
