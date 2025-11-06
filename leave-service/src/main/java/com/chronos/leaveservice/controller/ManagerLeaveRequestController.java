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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that manages leave request operations for managers.
 * <p>
 * Responsibilities:
 * - Retrieve all leave requests for a manager's team.
 * - Approve or reject leave requests.
 * - Retrieve statistical data about team leave requests.
 * - Retrieve leave request data formatted for manager dashboard display.
 * <p>
 * Base path: /api/leave-requests/manager
 * Security: Endpoints require MANAGER role.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "Manager Leave Request CRUD Rest API",
        description = "REST APIs - Get Team Leave Requests, Take Action on Leave Requests, Get Leave Request Stats, Get Manager Dashboard"
)
@Slf4j
@RestController
@RequestMapping("/api/leave-requests/manager")
public class ManagerLeaveRequestController {

    private final LeaveRequestServiceImpl leaveRequestService;

    @Autowired
    public ManagerLeaveRequestController(LeaveRequestServiceImpl leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    /**
     * Retrieve all leave requests for a manager's team members.
     * <p>
     * HTTP: GET /api/leave-requests/manager/{managerId}
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @return list of all leave requests for team members
     */

    @Operation(
            summary = "Get Team Leave Requests REST API",
            description = "Retrieve all leave requests for manager's team members"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team leave requests",
                    content = @Content(schema = @Schema(implementation = ManagerLeaveRequestDTO[].class))
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
    @GetMapping("/{managerId}")
    public ResponseEntity<List<ManagerLeaveRequestDTO>> getTeamLeaveRequests(@PathVariable("managerId") String managerId) {
        log.info("Invoked the GET: getTeamLeaveRequests controller method, managerId:{}", managerId);
        List<ManagerLeaveRequestDTO> getManagerLeaveRequest = leaveRequestService.getTeamLeaveRequests(managerId);
        return new ResponseEntity<>(getManagerLeaveRequest, HttpStatus.OK);
    }

    /**
     * Approve or reject a leave request.
     * <p>
     * HTTP: POST /api/leave-requests/manager/{requestId}/action?managerId={managerId}
     * Security: Requires MANAGER role.
     * <p>
     * The action (APPROVE/REJECT) is passed in the request body.
     *
     * @param managerId            the unique identifier of the manager taking action
     * @param requestId            the unique identifier of the leave request
     * @param leaveRequestActionDTO the action to perform (approve/reject)
     */

    @Operation(
            summary = "Take Action on Leave Request REST API",
            description = "Approve or reject a leave request (action passed in request body)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully processed leave request action"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data or action",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Manager or leave request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{requestId}/action")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actionOnLeaveRequest(@RequestParam String managerId, @PathVariable String requestId, @Valid @RequestBody LeaveRequestActionDTO leaveRequestActionDTO
    ) {
        log.info("Invoked the POST: actionOnLeaveRequest controller method, managerId:{}, requestId:{}, leaveRequestActionDTO:{}", managerId, requestId, leaveRequestActionDTO);
        leaveRequestService.actionOnLeaveRequest(managerId, requestId, leaveRequestActionDTO);
    }

    /**
     * Retrieve statistical data about leave requests for a manager's team.
     * <p>
     * HTTP: GET /api/leave-requests/manager/{managerId}/stats
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @return statistical data about team leave requests
     */

    @Operation(
            summary = "Get Leave Request Stats REST API",
            description = "Retrieve statistical data about leave requests for manager's team"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved leave request statistics",
                    content = @Content(schema = @Schema(implementation = ManagerLeaveRequestDataDTO.class))
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
    @GetMapping("{managerId}/stats")
    public ResponseEntity<ManagerLeaveRequestDataDTO> getTeamsLeaveRequestStats(@PathVariable("managerId") String managerId) {
        log.info("Invoked the GET: getTeamsLeaveRequestStats controller method, managerId:{}", managerId);
        ManagerLeaveRequestDataDTO stats = leaveRequestService.getLeaveRequestsStatsByManager(managerId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * Retrieve leave request data formatted for manager dashboard display.
     * <p>
     * HTTP: GET /api/leave-requests/manager/{managerId}/dashboard
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @return list of leave requests formatted for dashboard view
     */

    @Operation(
            summary = "Get Leave Request Dashboard REST API",
            description = "Retrieve leave request data formatted for manager dashboard display"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved manager dashboard data",
                    content = @Content(schema = @Schema(implementation = ManagerLeaveRequestDashboardResponseDTO[].class))
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
    @GetMapping("/{managerId}/dashboard")
    public ResponseEntity<List<ManagerLeaveRequestDashboardResponseDTO>> getLeaveRequestManagerDashboard(@PathVariable("managerId") String managerId) {
        log.info("Invoked the GET: getLeaveRequestManagerDashboard controller method, managerId:{}", managerId);
        List<ManagerLeaveRequestDashboardResponseDTO> leaveRequestData = leaveRequestService.getLeaveRequestManagerDashboard(managerId);
        return new ResponseEntity<>(leaveRequestData, HttpStatus.OK);
    }
}
