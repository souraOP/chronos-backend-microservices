package com.chronos.shiftservice.service;

import com.chronos.shiftservice.dto.shift.CreateShiftDateRequestDTO;
import com.chronos.shiftservice.dto.shift.ShiftResponseDTO;
import com.chronos.shiftservice.dto.shift.TeamShiftTableRowDTO;

import java.time.LocalDate;
import java.util.List;

public interface ShiftService {
    ShiftResponseDTO createShift(CreateShiftDateRequestDTO request, String managerId);

    List<ShiftResponseDTO> getEmployeeShifts(String employeeId);

    List<ShiftResponseDTO> getTeamsShiftByManager(String managerId);

    List<TeamShiftTableRowDTO> getTeamShiftsByManagerAndDatePicker(String managerId, LocalDate date);
}
