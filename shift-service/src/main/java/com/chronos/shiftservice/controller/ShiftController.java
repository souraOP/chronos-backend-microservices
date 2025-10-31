package com.chronos.shiftservice.controller;


import com.chronos.shiftservice.dto.shift.CreateShiftDateRequestDTO;
import com.chronos.shiftservice.dto.shift.ShiftResponseDTO;
import com.chronos.shiftservice.dto.shift.TeamShiftTableRowDTO;
import com.chronos.shiftservice.service.impl.ShiftServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/shifts")
@CrossOrigin("*")
public class ShiftController {
    private final ShiftServiceImpl shiftService;

    @Autowired
    public ShiftController(ShiftServiceImpl shiftService) {
        this.shiftService = shiftService;
    }


    @PostMapping("/manager/{managerId}/create")
    public ResponseEntity<ShiftResponseDTO> createShift(@PathVariable String managerId, @Valid @RequestBody CreateShiftDateRequestDTO request) {
        ShiftResponseDTO createdShift = shiftService.createShift(request, managerId);
        return new ResponseEntity<>(createdShift, HttpStatus.CREATED);
    }


    @GetMapping("/{employeeId}")
    public ResponseEntity<List<ShiftResponseDTO>> getEmployeeShifts(@PathVariable String employeeId) {
        List<ShiftResponseDTO> shifts = shiftService.getEmployeeShifts(employeeId);
        return ResponseEntity.ok(shifts);
    }



    @GetMapping("/manager/{managerId}/team-shifts")
    public ResponseEntity<List<ShiftResponseDTO>> getTeamsShiftByManager(@PathVariable String managerId) {
        List<ShiftResponseDTO> shifts = shiftService.getTeamsShiftByManager(managerId);
        return ResponseEntity.ok(shifts);
    }



    @GetMapping("/manager/{managerId}/team-shifts-by-date")
    public ResponseEntity<List<TeamShiftTableRowDTO>> getTeamShiftsByManagerAndDatePicker(
            @PathVariable String managerId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate date
    ) {
        List<TeamShiftTableRowDTO> response = shiftService.getTeamShiftsByManagerAndDatePicker(managerId, date);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
