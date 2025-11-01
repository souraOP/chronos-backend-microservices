package com.chronos.attendanceservice.util.mapper;


import com.chronos.attendanceservice.dto.attendance.AttendanceResponseDTO;
import com.chronos.attendanceservice.entity.Attendance;

public class AttendanceMapper {
    private AttendanceMapper() {}

    public static AttendanceResponseDTO attendanceEntityToDto(Attendance a) {
        return new AttendanceResponseDTO(
                a.getAttendanceId(),
                a.getDate(),
                a.getCheckIn(),
                a.getCheckOut(),
                a.getHoursWorked(),
                a.getAttendanceStatus(),
                a.getLocation()
        );
    }
}
