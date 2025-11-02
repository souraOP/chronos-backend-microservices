package com.chronos.attendanceservice.controller;


import com.chronos.attendanceservice.dto.AttendanceResponseDTO;
import com.chronos.attendanceservice.dto.CheckInRequestDTO;
import com.chronos.attendanceservice.dto.ManagerAttendanceDisplayByDateResponseDTO;
import com.chronos.attendanceservice.service.impl.AttendanceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Attendance CRUD Rest API",
        description = "REST APIs - Check-in, Check-out, Get Latest Attendance, Get Attendance History, Get Team Attendance By Date"
)
@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final AttendanceServiceImpl attendanceService;

    @Autowired
    public AttendanceController(AttendanceServiceImpl attendanceService) {
        this.attendanceService = attendanceService;
    }


    @Operation(
            summary = "Get Latest Attendance REST API",
            description = "Fetches the most recent attendance record for a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved latest attendance",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid employee ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee or attendance record not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}/latest")
    public ResponseEntity<AttendanceResponseDTO> getLatestAttendance(@PathVariable String employeeId) {
        AttendanceResponseDTO latestAttendance = attendanceService.getLatestAttendance(employeeId);
        return new ResponseEntity<>(latestAttendance, HttpStatus.OK);
    }



    @Operation(
            summary = "Get Attendance History REST API",
            description = "Fetches the entire attendance history for a specific employee."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved attendance history",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid employee ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
//    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}/history")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceHistory(@PathVariable String employeeId) {
        List<AttendanceResponseDTO> history = attendanceService.getAttendanceHistory(employeeId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }



    @Operation(
            summary = "Check-in Employee REST API",
            description = "Creates a check-in record for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully checked in",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{employeeId}/check-in")
    public ResponseEntity<AttendanceResponseDTO> checkIn(@PathVariable String employeeId,
                                                         @Valid @RequestBody(required = false) CheckInRequestDTO checkInRequestDTO) {
        AttendanceResponseDTO response = attendanceService.checkIn(employeeId, checkInRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }





    @Operation(
            summary = "Check-out Employee REST API",
            description = "Creates a check-out record for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully checked out",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid employee ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{employeeId}/check-out")
    public ResponseEntity<AttendanceResponseDTO> checkOut(@PathVariable String employeeId) {
        AttendanceResponseDTO response = attendanceService.checkOut(employeeId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    @Operation(
            summary = "Get Team Attendance By Date REST API",
            description = "Fetches manager's entire team attendance record for a specific date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team attendance",
                    content = @Content(schema = @Schema(implementation = ManagerAttendanceDisplayByDateResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID or date format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{managerId}/attendance")
    public ResponseEntity<ManagerAttendanceDisplayByDateResponseDTO> getTeamAttendanceByDate(@PathVariable String managerId,
                                                                                             @RequestParam("date") String date) {
        ManagerAttendanceDisplayByDateResponseDTO teamAttendance = attendanceService.getTeamsAttendanceByDate(managerId, date);
        return new ResponseEntity<>(teamAttendance, HttpStatus.OK);
    }
}
