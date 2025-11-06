package com.chronos.leaveservice.controller;

import com.chronos.leaveservice.dto.leaveRequests.EmployeeLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestCreateRequestDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestResponseDTO;
import com.chronos.leaveservice.service.impl.LeaveRequestServiceImpl;
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
 * REST controller that manages employee leave request operations.
 * <p>
 * Responsibilities:
 * - Create new leave requests for employees.
 * - Retrieve all leave requests for a specific employee.
 * - Retrieve leave request data formatted for employee dashboard display.
 * <p>
 * Base path: /api/leave-requests/employees
 * Security: Endpoints require EMPLOYEE role.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "Leave CRUD Rest API",
        description = "REST APIs - Create Leave Request, Get Leave Requests by Employee, Get Leave Request Employee Dashboard"
)
@Slf4j
@RestController
@RequestMapping("/api/leave-requests/employees")
public class LeaveController {
    private final LeaveRequestServiceImpl leaveRequestService;

    @Autowired
    public LeaveController(LeaveRequestServiceImpl leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    /**
     * Submit a new leave request for an employee.
     * <p>
     * HTTP: POST /api/leave-requests/employees/{employeeId}
     * Security: Requires EMPLOYEE role.
     *
     * @param employeeId the unique identifier of the employee
     * @param requestDTO the leave request details
     * @return the created leave request details
     */

    @Operation(
            summary = "Create Leave Request REST API",
            description = "Submit a new leave request for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully created leave request",
                    content = @Content(schema = @Schema(implementation = LeaveRequestResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data or validation failed"
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
    @PostMapping("/{employeeId}")
    public ResponseEntity<LeaveRequestResponseDTO> createLeaveRequest(@PathVariable("employeeId") String employeeId, @Valid @RequestBody LeaveRequestCreateRequestDTO requestDTO) {
        log.info("Invoked the POST: createLeaveRequest controller method, employeeId:{}, requestDTO:{}", employeeId, requestDTO);
        LeaveRequestResponseDTO createdLR = leaveRequestService.createLeaveRequest(employeeId, requestDTO);
        return new ResponseEntity<>(createdLR, HttpStatus.CREATED);
    }

    /**
     * Retrieve all leave requests for a specific employee.
     * <p>
     * HTTP: GET /api/leave-requests/employees/{employeeId}
     * Security: Requires EMPLOYEE role.
     *
     * @param employeeId the unique identifier of the employee
     * @return list of all leave requests for the employee
     */

    @Operation(
            summary = "Get Leave Requests By Employee REST API",
            description = "Retrieve all leave requests for a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved leave requests",
                    content = @Content(schema = @Schema(implementation = LeaveRequestResponseDTO[].class))
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
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveRequestByEmployee(@PathVariable("employeeId") String employeeId) {
        log.info("Invoked the GET: getLeaveRequestByEmployee controller method, employeeId:{}", employeeId);
        List<LeaveRequestResponseDTO> getLeaveRequests = leaveRequestService.getEmployeeLeaveRequests(employeeId);
        return new ResponseEntity<>(getLeaveRequests, HttpStatus.OK);
    }

    /**
     * Retrieve leave request data formatted for employee dashboard display.
     * <p>
     * HTTP: GET /api/leave-requests/employees/{employeeId}/dashboard
     * Security: Requires EMPLOYEE role.
     *
     * @param employeeId the unique identifier of the employee
     * @return list of leave requests formatted for dashboard view
     */

    @Operation(
            summary = "Get Leave Request Employee Dashboard REST API",
            description = "Retrieve leave request data formatted for employee dashboard display"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved dashboard data",
                    content = @Content(schema = @Schema(implementation = EmployeeLeaveRequestDashboardResponseDTO[].class))
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
    @GetMapping("/{employeeId}/dashboard")
    public ResponseEntity<List<EmployeeLeaveRequestDashboardResponseDTO>> getLeaveRequestEmployeeDashboard(@PathVariable("employeeId") String employeeId) {
        log.info("Invoked the GET: getLeaveRequestEmployeeDashboard controller method, employeeId:{}", employeeId);
        List<EmployeeLeaveRequestDashboardResponseDTO> getLeaveRequestDashboard = leaveRequestService.getLeaveRequestEmployeeDashboard(employeeId);
        return new ResponseEntity<>(getLeaveRequestDashboard, HttpStatus.OK);
    }
}
