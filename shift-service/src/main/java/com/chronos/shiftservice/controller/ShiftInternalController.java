package com.chronos.shiftservice.controller;

import com.chronos.shiftservice.dto.shift.ShiftCardDTO;
import com.chronos.shiftservice.dto.shift.UpcomingShiftsRequestDTO;
import com.chronos.shiftservice.service.impl.ShiftServiceInternalImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Internal REST controller for shift data retrieval operations.
 * <p>
 * This controller provides internal APIs used by other microservices within the platform
 * for retrieving shift information without authentication checks.
 * <p>
 * Responsibilities:
 * - Retrieve upcoming shifts for multiple employees by their IDs.
 * - Support internal inter-service communication for shift data.
 * <p>
 * Base path: /api/shifts/internal
 * Security: Internal endpoints - intended for service-to-service communication.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Slf4j
@RestController
@RequestMapping("/api/shifts/internal")
public class ShiftInternalController {
    private final ShiftServiceInternalImpl shiftServiceInternal;

    public ShiftInternalController(ShiftServiceInternalImpl shiftServiceInternal) {
        this.shiftServiceInternal = shiftServiceInternal;
    }

    /**
     * Retrieve upcoming shifts for multiple employees.
     * <p>
     * HTTP: POST /api/shifts/internal/upcoming-by-employee-ids
     * Security: Internal endpoint for inter-service communication.
     * <p>
     * Bulk retrieves upcoming shift information for a list of employee IDs,
     * returning shifts organized by employee UUID.
     *
     * @param request the request containing the list of employee IDs to query
     * @return a map of employee UUIDs to their upcoming shifts
     */

    @PostMapping("/upcoming-by-employee-ids")
    public ResponseEntity<Map<UUID, List<ShiftCardDTO>>> getUpcomingByEmployeeIds(@RequestBody UpcomingShiftsRequestDTO request) {
        log.info("Invoked the POST: getUpcomingByEmployeeIds controller methods, upcomingShiftRequestDTO:{}", request);
        Map<UUID, List<ShiftCardDTO>> getShifts = shiftServiceInternal.getUpcomingShiftsByEmployeeIds(request);
        return new ResponseEntity<>(getShifts, HttpStatus.OK);
    }
}
