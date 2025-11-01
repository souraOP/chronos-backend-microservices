package com.chronos.shiftservice.service;


import com.chronos.shiftservice.dto.shiftSwapRequest.CreateShiftSwapRequestDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapQueryResponseDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapResponseDTO;

import java.util.List;

public interface ShiftSwapRequestService {
    ShiftSwapResponseDTO createSwapRequest(CreateShiftSwapRequestDTO createSwapDto);

    List<ShiftSwapQueryResponseDTO> getSwapRequestsForEmployee(String employeeId);

    List<ShiftSwapQueryResponseDTO> getTeamSwapRequests(String managerId);

    ShiftSwapResponseDTO approveSwapRequest(String managerId, String swapRequestId);

    ShiftSwapResponseDTO rejectSwapRequest(String managerId, String swapRequestId);
}
