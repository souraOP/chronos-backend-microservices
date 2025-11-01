package com.chronos.attendanceservice.dto.attendance;


import com.chronos.common.constants.enums.AttendanceStatus;

import java.time.OffsetDateTime;

public record ManagerAttendanceRowDTO(
    String displayEmployeeId,
    String employeeName,
    OffsetDateTime checkIn,
    OffsetDateTime checkOut,
    double hoursWorked,
    AttendanceStatus attendanceStatus
) {
}
