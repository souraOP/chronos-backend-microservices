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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that manages employee attendance operations.
 * <p>
 * Responsibilities:
 * - Record employee check-in events.
 * - Record employee check-out events.
 * - Retrieve the latest attendance record for an employee.
 * - Retrieve complete attendance history for an employee.
 * - Retrieve team attendance for a specific date (manager view).
 * <p>
 * Base path: /api/attendances
 * Security: Endpoints are protected and require appropriate roles as noted per method.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "Attendance CRUD Rest API",
        description = "REST APIs - Check-in, Check-out, Get Latest Attendance, Get Attendance History, Get Team Attendance By Date"
)
@Slf4j
@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final AttendanceServiceImpl attendanceService;

    @Autowired
    public AttendanceController(AttendanceServiceImpl attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Retrieve the most recent attendance record for a specific employee.
     * <p>
     * HTTP: GET /api/attendances/{employeeId}/latest
     * Security: Requires EMPLOYEE role.
     *
     * @param employeeId the unique identifier of the employee
     * @return the latest attendance record
     */

    @Operation(
            summary = "Get Latest Attendance REST API",
            description = "Fetches the most recent attendance record for a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved latest attendance"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid employee ID format"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Employee or attendance record not found"
            )
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}/latest")
    public ResponseEntity<AttendanceResponseDTO> getLatestAttendance(@PathVariable String employeeId) {
        log.info("Invoked the GET: getLatestAttendance controller method, employeeId:{}", employeeId);
        AttendanceResponseDTO latestAttendance = attendanceService.getLatestAttendance(employeeId);
        return new ResponseEntity<>(latestAttendance, HttpStatus.OK);
    }

    /**
     * Retrieve the entire attendance history for a specific employee.
     * <p>
     * HTTP: GET /api/attendances/{employeeId}/history
     * Security: Open endpoint (EMPLOYEE role commented out).
     *
     * @param employeeId the unique identifier of the employee
     * @return list of all attendance records for the employee
     */

    @Operation(
            summary = "Get Attendance History REST API",
            description = "Fetches the entire attendance history for a specific employee."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved attendance history",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDTO[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid employee ID format"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Employee not found"
            )
    })
    @GetMapping("/{employeeId}/history")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceHistory(@PathVariable String employeeId) {
        log.info("Invoked the GET: getAttendanceHistory controller method, employeeId:{}", employeeId);
        List<AttendanceResponseDTO> history = attendanceService.getAttendanceHistory(employeeId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    /**
     * Record a check-in event for an employee.
     * <p>
     * HTTP: POST /api/attendances/{employeeId}/check-in
     * Security: Requires EMPLOYEE role.
     *
     * @param employeeId       the unique identifier of the employee
     * @param checkInRequestDTO optional check-in metadata (e.g., location)
     * @return the created attendance record with check-in timestamp
     */

    @Operation(
            summary = "Check-in Employee REST API",
            description = "Creates a check-in record for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully checked in",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Employee not found"
            )
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{employeeId}/check-in")
    public ResponseEntity<AttendanceResponseDTO> checkIn(@PathVariable String employeeId, @Valid @RequestBody(required = false) CheckInRequestDTO checkInRequestDTO) {
        log.info("Invoked the POST: checkIn controller method, employeeId:{}, checkInRequestDTO:{}", employeeId, checkInRequestDTO);
        AttendanceResponseDTO response = attendanceService.checkIn(employeeId, checkInRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Record a check-out event for an employee.
     * <p>
     * HTTP: POST /api/attendances/{employeeId}/check-out
     * Security: Requires EMPLOYEE role.
     *
     * @param employeeId the unique identifier of the employee
     * @return the updated attendance record with check-out timestamp
     */

    @Operation(
            summary = "Check-out Employee REST API",
            description = "Creates a check-out record for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully checked out",
                    content = @Content(schema = @Schema(implementation = AttendanceResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid employee ID format"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Employee not found"
            )
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{employeeId}/check-out")
    public ResponseEntity<AttendanceResponseDTO> checkOut(@PathVariable String employeeId) {
        log.info("Invoked the POST: checkOut controller method, employeeId:{}", employeeId);
        AttendanceResponseDTO response = attendanceService.checkOut(employeeId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve attendance records for a manager's entire team on a specific date.
     * <p>
     * HTTP: GET /api/attendances/{managerId}/attendance?date={date}
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @param date      the date to filter team attendance (format: ISO date string)
     * @return team attendance data for the specified date
     */

    @Operation(
            summary = "Get Team Attendance By Date REST API",
            description = "Fetches manager's entire team attendance record for a specific date"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team attendance"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid manager ID or date format"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Manager not found"
            )
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{managerId}/attendance")
    public ResponseEntity<ManagerAttendanceDisplayByDateResponseDTO> getTeamAttendanceByDate(@PathVariable String managerId, @RequestParam("date") String date) {
        log.info("Invoked the GET: getTeamAttendanceByDate controller method, managerId:{}, date:{}", managerId, date);
        ManagerAttendanceDisplayByDateResponseDTO teamAttendance = attendanceService.getTeamsAttendanceByDate(managerId, date);
        return new ResponseEntity<>(teamAttendance, HttpStatus.OK);
    }
}
