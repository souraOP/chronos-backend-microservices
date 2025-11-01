package com.chronos.attendanceservice.controller;


import com.chronos.attendanceservice.dto.attendance.AttendanceResponseDTO;
import com.chronos.attendanceservice.dto.attendance.CheckInRequestDTO;
import com.chronos.attendanceservice.dto.attendance.ManagerAttendanceDisplayByDateResponseDTO;
import com.chronos.attendanceservice.service.impl.AttendanceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@CrossOrigin("*")
public class AttendanceController {
    private final AttendanceServiceImpl attendanceService;

    @Autowired
    public AttendanceController(AttendanceServiceImpl attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}/latest")
    public ResponseEntity<AttendanceResponseDTO> getLatestAttendance(@PathVariable String employeeId) {
        AttendanceResponseDTO latestAttendance = attendanceService.getLatestAttendance(employeeId);
        return new ResponseEntity<>(latestAttendance, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}/history")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceHistory(@PathVariable String employeeId) {
        List<AttendanceResponseDTO> history = attendanceService.getAttendanceHistory(employeeId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{employeeId}/check-in")
    public ResponseEntity<AttendanceResponseDTO> checkIn(@PathVariable String employeeId,
                                                         @Valid @RequestBody(required = false) CheckInRequestDTO checkInRequestDTO) {
        AttendanceResponseDTO response = attendanceService.checkIn(employeeId, checkInRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{employeeId}/check-out")
    public ResponseEntity<AttendanceResponseDTO> checkOut(@PathVariable String employeeId) {
        AttendanceResponseDTO response = attendanceService.checkOut(employeeId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{managerId}/attendance")
    public ResponseEntity<ManagerAttendanceDisplayByDateResponseDTO> getTeamAttendanceByDate(@PathVariable String managerId,
                                                                                             @RequestParam("date") String date) {
        ManagerAttendanceDisplayByDateResponseDTO teamAttendance = attendanceService.getTeamsAttendanceByDate(managerId, date);
        return new ResponseEntity<>(teamAttendance, HttpStatus.OK);
    }
}
