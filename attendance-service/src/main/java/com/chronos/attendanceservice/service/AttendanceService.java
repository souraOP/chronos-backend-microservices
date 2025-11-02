package com.chronos.attendanceservice.service;


import com.chronos.attendanceservice.dto.AttendanceResponseDTO;
import com.chronos.attendanceservice.dto.CheckInRequestDTO;
import com.chronos.attendanceservice.dto.ManagerAttendanceDisplayByDateResponseDTO;

import java.util.List;

public interface AttendanceService {
    AttendanceResponseDTO getLatestAttendance(String employeeId);

    List<AttendanceResponseDTO> getAttendanceHistory(String employeeId);

    AttendanceResponseDTO checkIn(String employeeId, CheckInRequestDTO checkInRequestDTO);

    AttendanceResponseDTO checkOut(String employeeId);

    ManagerAttendanceDisplayByDateResponseDTO getTeamsAttendanceByDate(String managerId, String date);
}
