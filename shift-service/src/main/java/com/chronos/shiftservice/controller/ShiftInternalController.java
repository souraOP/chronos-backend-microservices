package com.chronos.shiftservice.controller;

import com.chronos.shiftservice.dto.shift.ShiftCardDTO;
import com.chronos.shiftservice.dto.shift.UpcomingShiftsRequestDTO;
import com.chronos.shiftservice.service.impl.ShiftServiceInternalImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/shifts/internal")
public class ShiftInternalController {
    private final ShiftServiceInternalImpl shiftServiceInternal;

    public ShiftInternalController(ShiftServiceInternalImpl shiftServiceInternal) {
        this.shiftServiceInternal = shiftServiceInternal;
    }

    @PostMapping("/upcoming-by-employee-ids")
    public ResponseEntity<Map<UUID, List<ShiftCardDTO>>> getUpcomingByEmployeeIds(@RequestBody UpcomingShiftsRequestDTO request) {
        Map<UUID, List<ShiftCardDTO>> getShifts = shiftServiceInternal.getUpcomingShiftsByEmployeeIds(request);
        return new ResponseEntity<>(getShifts, HttpStatus.OK);
    }
}
