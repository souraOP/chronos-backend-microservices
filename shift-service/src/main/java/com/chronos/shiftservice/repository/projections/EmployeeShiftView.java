package com.chronos.shiftservice.repository.projections;


import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.constants.enums.ShiftType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface EmployeeShiftView {
        UUID getId();

        String getShiftId();

        UUID getEmployeeId();

        LocalDate getShiftDate();

        OffsetDateTime getShiftStartTime();

        OffsetDateTime getShiftEndTime();

        ShiftStatus getShiftStatus();

        ShiftType getShiftType();

        String getShiftLocation();
}