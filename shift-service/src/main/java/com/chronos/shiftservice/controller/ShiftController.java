package com.chronos.shiftservice.controller;


import com.chronos.common.exception.ErrorResponse;
import com.chronos.shiftservice.dto.shift.CreateShiftDateRequestDTO;
import com.chronos.shiftservice.dto.shift.ShiftResponseDTO;
import com.chronos.shiftservice.dto.shift.TeamShiftTableRowDTO;
import com.chronos.shiftservice.service.impl.ShiftServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller that manages shift assignments for employees and teams.
 * <p>
 * Responsibilities:
 * - Create shift assignments for a manager's team.
 * - Retrieve shifts for a specific employee.
 * - Retrieve all shifts for a manager's team.
 * - Retrieve team shifts for a specific date.
 * <p>
 * Base path: /api/shifts
 * Security: Endpoints are protected and require appropriate roles as noted per method.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "Shift CRUD Rest API",
        description = "REST APIs - Create Shift, Get Employee Shifts, Get Team Shifts, Get Team Shifts By Date"
)
@RestController
@RequestMapping("/api/shifts")
public class ShiftController {
    private final ShiftServiceImpl shiftService;

    private static final Logger log = LoggerFactory.getLogger(ShiftController.class);

    @Autowired
    public ShiftController(ShiftServiceImpl shiftService) {
        this.shiftService = shiftService;
    }

    /**
     * Create a new shift assignment for a manager's team members.
     * <p>
     * HTTP: POST /api/shifts/manager/{managerId}/create
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager creating the shift
     * @param request   the shift creation payload containing date(s), employees, and shift details
     * @return the created shift information
     */

    @Operation(
            summary = "Create Shift REST API",
            description = "Create a new shift assignment for manager's team members"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully created shift",
                    content = @Content(schema = @Schema(implementation = ShiftResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data or manager ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/{managerId}/create")
    public ResponseEntity<ShiftResponseDTO> createShift(@PathVariable String managerId, @Valid @RequestBody CreateShiftDateRequestDTO request) {
        log.info("Invoked the POST: createShift controller method, managerId={}, createShiftDateRequestDTO={}", managerId, request);
        ShiftResponseDTO createdShift = shiftService.createShift(request, managerId);
        return new ResponseEntity<>(createdShift, HttpStatus.CREATED);
    }

    /**
     * Retrieve all shift assignments for a specific employee.
     * <p>
     * HTTP: GET /api/shifts/{employeeId}
     * Security: Requires EMPLOYEE role.
     *
     * @param employeeId the unique identifier of the employee
     * @return list of shifts associated with the employee
     */

    @Operation(
            summary = "Get Employee Shifts REST API",
            description = "Retrieve all shift assignments for a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved employee shifts",
                    content = @Content(schema = @Schema(implementation = ShiftResponseDTO[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid employee ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<ShiftResponseDTO>> getEmployeeShifts(@PathVariable String employeeId) {
        log.info("Invoked the GET: getEmployeeShifts controller method, employeeId={}", employeeId);
        List<ShiftResponseDTO> shifts = shiftService.getEmployeeShifts(employeeId);
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    /**
     * Retrieve all shift assignments for a manager's entire team.
     * <p>
     * HTTP: GET /api/shifts/manager/{managerId}/team-shifts
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @return list of shifts for all team members under the manager
     */

    @Operation(
            summary = "Get Team Shifts REST API",
            description = "Retrieve all shift assignments for manager's entire team"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team shifts",
                    content = @Content(schema = @Schema(implementation = ShiftResponseDTO[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/team-shifts")
    public ResponseEntity<List<ShiftResponseDTO>> getTeamsShiftByManager(@PathVariable String managerId) {
        log.info("Invoked the GET: getTeamsShiftByManager controller method, managerId={}", managerId);
        List<ShiftResponseDTO> shifts = shiftService.getTeamsShiftByManager(managerId);
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    /**
     * Retrieve team shift assignments filtered by a specific date.
     * <p>
     * HTTP: GET /api/shifts/manager/{managerId}/team-shifts-by-date?date={ISO_DATE}
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @param date      the date (ISO format) to filter team shifts
     * @return a tabular view model of team shifts for the specified date
     */

    @Operation(
            summary = "Get Team Shifts By Date REST API",
            description = "Retrieve team shift assignments filtered by specific date"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team shifts by date",
                    content = @Content(schema = @Schema(implementation = TeamShiftTableRowDTO[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid manager ID or date format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/team-shifts-by-date")
    public ResponseEntity<List<TeamShiftTableRowDTO>> getTeamShiftsByManagerAndDatePicker(
            @PathVariable String managerId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate date
    ) {
        log.info("Invoked the GET: getTeamShiftsByManagerAndDatePicker controller method, managerId={}, date={}", managerId, date);
        List<TeamShiftTableRowDTO> response = shiftService.getTeamShiftsByManagerAndDatePicker(managerId, date);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
