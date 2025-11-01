package com.chronos.shiftservice.controller;


import com.chronos.shiftservice.dto.shiftSwapRequest.CreateShiftSwapRequestDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapQueryResponseDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapResponseDTO;
import com.chronos.shiftservice.service.impl.ShiftSwapRequestServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift-swap-requests")
@CrossOrigin("*")
public class ShiftSwapRequestController {
    private final ShiftSwapRequestServiceImpl shiftSwapRequestService;

    @Autowired
    public ShiftSwapRequestController(ShiftSwapRequestServiceImpl shiftSwapRequestService) {
        this.shiftSwapRequestService = shiftSwapRequestService;
    }


    @PostMapping("/create")
    public ResponseEntity<ShiftSwapResponseDTO> createSwapRequest(@Valid @RequestBody CreateShiftSwapRequestDTO createShiftSwapRequestDTO) {
        ShiftSwapResponseDTO createSwap = shiftSwapRequestService.createSwapRequest(createShiftSwapRequestDTO);
        return new ResponseEntity<>(createSwap, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ShiftSwapQueryResponseDTO>> getSwapRequestsForEmployee(@PathVariable String employeeId) {
        List<ShiftSwapQueryResponseDTO> getSwapRequestsByEmployee = shiftSwapRequestService.getSwapRequestsForEmployee(employeeId);
        return new ResponseEntity<>(getSwapRequestsByEmployee, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/requests")
    public ResponseEntity<List<ShiftSwapQueryResponseDTO>> getTeamSwapRequests(@PathVariable String managerId) {
        List<ShiftSwapQueryResponseDTO> getTeamsShift = shiftSwapRequestService.getTeamSwapRequests(managerId);
        return new ResponseEntity<>(getTeamsShift, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/{managerId}/requests/{swapRequestId}/approve")
    public ResponseEntity<ShiftSwapResponseDTO> approveSwapRequest(@PathVariable String managerId, @PathVariable String swapRequestId) {
        ShiftSwapResponseDTO approveSwapRequest = shiftSwapRequestService.approveSwapRequest(managerId, swapRequestId);
        return new ResponseEntity<>(approveSwapRequest, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/{managerId}/requests/{swapRequestId}/reject")
    public ResponseEntity<ShiftSwapResponseDTO> rejectSwapRequest(@PathVariable String managerId, @PathVariable String swapRequestId) {
        ShiftSwapResponseDTO rejectSwapRequest = shiftSwapRequestService.rejectSwapRequest(managerId, swapRequestId);
        return ResponseEntity.ok(rejectSwapRequest);
    }
}
