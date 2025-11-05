package com.chronos.leaveservice.controller;

import com.chronos.common.exception.ErrorResponse;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestActionDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDataDTO;
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
        name = "Manager Leave Request CRUD Rest API",
        description = "REST APIs - Get Team Leave Requests, Take Action on Leave Requests, Get Leave Request Stats, Get Manager Dashboard"
)
@RestController
@RequestMapping("/api/leave-requests/manager")
public class ManagerLeaveRequestController {

    private final LeaveRequestServiceImpl leaveRequestService;

    @Autowired
    public ManagerLeaveRequestController(LeaveRequestServiceImpl leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }




    @Operation(
            summary = "Get Team Leave Requests REST API",
            description = "Retrieve all leave requests for manager's team members"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team leave requests",
                    content = @Content(schema = @Schema(implementation = ManagerLeaveRequestDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{managerId}")
    public ResponseEntity<List<ManagerLeaveRequestDTO>> getTeamLeaveRequests(@PathVariable String managerId) {
        List<ManagerLeaveRequestDTO> getManagerLeaveRequest = leaveRequestService.getTeamLeaveRequests(managerId);
        return new ResponseEntity<>(getManagerLeaveRequest, HttpStatus.OK);
    }





    // have to pass the action in the request body
    @Operation(
            summary = "Take Action on Leave Request REST API",
            description = "Approve or reject a leave request (action passed in request body)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully processed leave request action"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or action",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager or leave request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{requestId}/action")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actionOnLeaveRequest(
            @RequestParam String managerId,
            @PathVariable String requestId,
            @Valid @RequestBody LeaveRequestActionDTO leaveRequestActionDTO
    ) {
        leaveRequestService.actionOnLeaveRequest(managerId, requestId, leaveRequestActionDTO);
    }




    @Operation(
            summary = "Get Leave Request Stats REST API",
            description = "Retrieve statistical data about leave requests for manager's team"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved leave request statistics",
                    content = @Content(schema = @Schema(implementation = ManagerLeaveRequestDataDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("{managerId}/stats")
    public ResponseEntity<ManagerLeaveRequestDataDTO> getTeamsLeaveRequestStats(@PathVariable String managerId) {
        ManagerLeaveRequestDataDTO stats = leaveRequestService.getLeaveRequestsStatsByManager(managerId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }




    @Operation(
            summary = "Get Leave Request Dashboard REST API",
            description = "Retrieve leave request data formatted for manager dashboard display"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved manager dashboard data",
                    content = @Content(schema = @Schema(implementation = ManagerLeaveRequestDashboardResponseDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{managerId}/dashboard")
    public ResponseEntity<List<ManagerLeaveRequestDashboardResponseDTO>> getLeaveRequestManagerDashboard(@PathVariable String managerId) {
        List<ManagerLeaveRequestDashboardResponseDTO> leaveRequestData = leaveRequestService.getLeaveRequestManagerDashboard(managerId);
        return new ResponseEntity<>(leaveRequestData, HttpStatus.OK);
    }
}
