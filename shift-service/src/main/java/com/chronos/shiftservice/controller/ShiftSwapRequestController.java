package com.chronos.shiftservice.controller;


import com.chronos.common.exception.ErrorResponse;
import com.chronos.shiftservice.dto.shiftSwapRequest.CreateShiftSwapRequestDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapQueryResponseDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapResponseDTO;
import com.chronos.shiftservice.service.impl.ShiftSwapRequestServiceImpl;
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
        name = "Shift Swap Request CRUD Rest API",
        description = "REST APIs - Create Shift Swap Request, Get Employee Swap Requests, Get Team Swap Requests, Approve/Reject Swap Requests"
)
@RestController
@RequestMapping("/api/shift-swap-requests")
public class ShiftSwapRequestController {
    private final ShiftSwapRequestServiceImpl shiftSwapRequestService;

    @Autowired
    public ShiftSwapRequestController(ShiftSwapRequestServiceImpl shiftSwapRequestService) {
        this.shiftSwapRequestService = shiftSwapRequestService;
    }




    @Operation(
            summary = "Create Shift Swap Request REST API",
            description = "Create a new shift swap request between employees"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created shift swap request",
                    content = @Content(schema = @Schema(implementation = ShiftSwapResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<ShiftSwapResponseDTO> createSwapRequest(@Valid @RequestBody CreateShiftSwapRequestDTO createShiftSwapRequestDTO) {
        ShiftSwapResponseDTO createSwap = shiftSwapRequestService.createSwapRequest(createShiftSwapRequestDTO);
        return new ResponseEntity<>(createSwap, HttpStatus.CREATED);
    }




    @Operation(
            summary = "Get Shift Swap Requests For Employee REST API",
            description = "Retrieve all shift swap requests related to a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved shift swap requests",
                    content = @Content(schema = @Schema(implementation = ShiftSwapQueryResponseDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid employee ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ShiftSwapQueryResponseDTO>> getSwapRequestsForEmployee(@PathVariable String employeeId) {
        List<ShiftSwapQueryResponseDTO> getSwapRequestsByEmployee = shiftSwapRequestService.getSwapRequestsForEmployee(employeeId);
        return new ResponseEntity<>(getSwapRequestsByEmployee, HttpStatus.OK);
    }




    @Operation(
            summary = "Get Team Shift Swap Requests REST API",
            description = "Retrieve all shift swap requests for manager's team members"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team shift swap requests",
                    content = @Content(schema = @Schema(implementation = ShiftSwapQueryResponseDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/requests")
    public ResponseEntity<List<ShiftSwapQueryResponseDTO>> getTeamSwapRequests(@PathVariable String managerId) {
        List<ShiftSwapQueryResponseDTO> getTeamsShift = shiftSwapRequestService.getTeamSwapRequests(managerId);
        return new ResponseEntity<>(getTeamsShift, HttpStatus.OK);
    }





    @Operation(
            summary = "Approve Shift Swap Request REST API",
            description = "Approve a pending shift swap request"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully approved shift swap request",
                    content = @Content(schema = @Schema(implementation = ShiftSwapResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID or swap request ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager or shift swap request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/{managerId}/requests/{swapRequestId}/approve")
    public ResponseEntity<ShiftSwapResponseDTO> approveSwapRequest(@PathVariable String managerId, @PathVariable String swapRequestId) {
        ShiftSwapResponseDTO approveSwapRequest = shiftSwapRequestService.approveSwapRequest(managerId, swapRequestId);
        return new ResponseEntity<>(approveSwapRequest, HttpStatus.OK);
    }





    @Operation(
            summary = "Reject Shift Swap Request REST API",
            description = "Reject a pending shift swap request"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully rejected shift swap request",
                    content = @Content(schema = @Schema(implementation = ShiftSwapResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID or swap request ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager or shift swap request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/{managerId}/requests/{swapRequestId}/reject")
    public ResponseEntity<ShiftSwapResponseDTO> rejectSwapRequest(@PathVariable String managerId, @PathVariable String swapRequestId) {
        ShiftSwapResponseDTO rejectSwapRequest = shiftSwapRequestService.rejectSwapRequest(managerId, swapRequestId);
        return ResponseEntity.ok(rejectSwapRequest);
    }
}
