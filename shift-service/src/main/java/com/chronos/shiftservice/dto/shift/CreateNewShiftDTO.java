package com.chronos.shiftservice.dto.shift;

import com.chronos.shiftservice.constants.enums.ShiftStatus;
import com.chronos.shiftservice.constants.enums.ShiftType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateNewShiftDTO(
        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        @NotNull(message = "Shift date is required")
        @FutureOrPresent(message = "Shift date must be today or in the future")
        LocalDate shiftDate,

        @NotNull(message = "Shift start time is required")
        @FutureOrPresent(message = "Shift start time must be present or future")
        OffsetDateTime shiftStartTime,

        @NotNull(message = "Shift end time is required")
        @FutureOrPresent(message = "Shift end time must be present or future")
        OffsetDateTime shiftEndTime,

        @NotNull(message = "Shift status is required")
        ShiftStatus shiftStatus,

        @NotNull(message = "Shift type is required")
        ShiftType shiftType,

        @NotNull(message = "Shift location is required")
        @Size(min = 5, max = 50, message = "Shift location must be between 5 and 50 characters")
        String shiftLocation
) {
}
