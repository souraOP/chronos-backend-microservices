package com.chronos.employeeservice.dto;


import com.chronos.employeeservice.constants.enums.ShiftStatus;
import com.chronos.employeeservice.constants.enums.ShiftType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ShiftCardDTO(
        UUID id,
        String shiftId,
        OffsetDateTime shiftDate,
        OffsetDateTime shiftStartTime,
        OffsetDateTime shiftEndTime,
        String shiftLocation,
        ShiftType shiftType,
        ShiftStatus shiftStatus
) {
}
