package com.chronos.attendanceservice.dto;


import com.chronos.common.constants.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record AttendanceResponseDTO(
        String attendanceId,
        LocalDate date,
        OffsetDateTime checkIn,
        OffsetDateTime checkOut,
        double hoursWorked,
        AttendanceStatus attendanceStatus,
        String location
) {
}
