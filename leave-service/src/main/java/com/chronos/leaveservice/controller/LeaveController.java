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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(
        name = "Leave CRUD Rest API",
        description = "REST APIs - Create Leave Request, Get Leave Requests by Employee, Get Leave Request Employee Dashboard"
)
@RestController
@RequestMapping("/api/leave-requests/employees")
@CrossOrigin("*")
public class LeaveController {
    private final LeaveRequestServiceImpl leaveRequestService;

    @Autowired
    public LeaveController(LeaveRequestServiceImpl leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }



    @Operation(
            summary = "Create Leave Request REST API",
            description = "Submit a new leave request for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created leave request",
                    content = @Content(schema = @Schema(implementation = LeaveRequestResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{employeeId}")
    public ResponseEntity<LeaveRequestResponseDTO> createLeaveRequest(@PathVariable("employeeId") String employeeId, @Valid @RequestBody LeaveRequestCreateRequestDTO requestDTO) {
        LeaveRequestResponseDTO createdLR = leaveRequestService.createLeaveRequest(employeeId, requestDTO);
        return ResponseEntity.ok(createdLR);
    }



    @Operation(
            summary = "Get Leave Requests By Employee REST API",
            description = "Retrieve all leave requests for a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved leave requests",
                    content = @Content(schema = @Schema(implementation = LeaveRequestResponseDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid employee ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveRequestByEmployee(@PathVariable("employeeId") String employeeId) {
        List<LeaveRequestResponseDTO> getLeaveRequests = leaveRequestService.getEmployeeLeaveRequests(employeeId);
        return ResponseEntity.ok(getLeaveRequests);
    }




    @Operation(
            summary = "Get Leave Request Employee Dashboard REST API",
            description = "Retrieve leave request data formatted for employee dashboard display"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved dashboard data",
                    content = @Content(schema = @Schema(implementation = EmployeeLeaveRequestDashboardResponseDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid employee ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}/dashboard")
    public ResponseEntity<List<EmployeeLeaveRequestDashboardResponseDTO>> getLeaveRequestEmployeeDashboard(@PathVariable String employeeId) {
        List<EmployeeLeaveRequestDashboardResponseDTO> getLeaveRequestDashboard = leaveRequestService.getLeaveRequestEmployeeDashboard(employeeId);
        return new ResponseEntity<>(getLeaveRequestDashboard, HttpStatus.OK);
    }

}
