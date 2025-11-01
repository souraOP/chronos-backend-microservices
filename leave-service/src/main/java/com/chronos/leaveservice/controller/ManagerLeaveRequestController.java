package com.chronos.leaveservice.controller;

import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestActionDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDataDTO;
import com.chronos.leaveservice.service.impl.LeaveRequestServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/leave-requests/manager")
@CrossOrigin("*")
public class ManagerLeaveRequestController {

    private final LeaveRequestServiceImpl leaveRequestService;

    @Autowired
    public ManagerLeaveRequestController(LeaveRequestServiceImpl leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }


    @GetMapping("/{managerId}")
    public ResponseEntity<List<ManagerLeaveRequestDTO>> getTeamLeaveRequests(@PathVariable String managerId) {
        List<ManagerLeaveRequestDTO> getManagerLeaveRequest = leaveRequestService.getTeamLeaveRequests(managerId);
        return ResponseEntity.ok(getManagerLeaveRequest);
    }

    // have to pass the action in the request body
    @PostMapping("/{requestId}/action")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actionOnLeaveRequest(
            @RequestParam String managerId,
            @PathVariable String requestId,
            @Valid @RequestBody LeaveRequestActionDTO leaveRequestActionDTO
    ) {
        leaveRequestService.actionOnLeaveRequest(managerId, requestId, leaveRequestActionDTO);
    }


    @GetMapping("/stats")
    public ResponseEntity<ManagerLeaveRequestDataDTO> getTeamsLeaveRequestStats(@RequestParam String managerId) {
        ManagerLeaveRequestDataDTO stats = leaveRequestService.getLeaveRequestsStatsByManager(managerId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }


    @GetMapping("/{managerId}/dashboard")
    public ResponseEntity<List<ManagerLeaveRequestDashboardResponseDTO>> getLeaveRequestManagerDashboard(@PathVariable String managerId) {
        List<ManagerLeaveRequestDashboardResponseDTO> leaveRequestData = leaveRequestService.getLeaveRequestManagerDashboard(managerId);
        return new ResponseEntity<>(leaveRequestData, HttpStatus.OK);
    }
}
