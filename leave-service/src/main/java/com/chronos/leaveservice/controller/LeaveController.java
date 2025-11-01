package com.chronos.leaveservice.controller;

import com.chronos.leaveservice.dto.leaveRequests.EmployeeLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestCreateRequestDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestResponseDTO;
import com.chronos.leaveservice.service.impl.LeaveRequestServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests/employees")
@CrossOrigin("*")
public class LeaveController {
    private final LeaveRequestServiceImpl leaveRequestService;

    @Autowired
    public LeaveController(LeaveRequestServiceImpl leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }


    @PostMapping("/{employeeId}")
    public ResponseEntity<LeaveRequestResponseDTO> createLeaveRequest(@PathVariable("employeeId") String employeeId, @Valid @RequestBody LeaveRequestCreateRequestDTO requestDTO) {
        LeaveRequestResponseDTO createdLR = leaveRequestService.createLeaveRequest(employeeId, requestDTO);
        return ResponseEntity.ok(createdLR);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveRequestByEmployee(@PathVariable("employeeId") String employeeId) {
        List<LeaveRequestResponseDTO> getLeaveRequests = leaveRequestService.getEmployeeLeaveRequests(employeeId);
        return ResponseEntity.ok(getLeaveRequests);
    }


    @GetMapping("/{employeeId}/dashboard")
    public ResponseEntity<List<EmployeeLeaveRequestDashboardResponseDTO>> getLeaveRequestEmployeeDashboard(@PathVariable String employeeId) {
        List<EmployeeLeaveRequestDashboardResponseDTO> getLeaveRequestDashboard = leaveRequestService.getLeaveRequestEmployeeDashboard(employeeId);
        return new ResponseEntity<>(getLeaveRequestDashboard, HttpStatus.OK);
    }

}
