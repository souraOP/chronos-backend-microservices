package com.chronos.shiftservice.controller;

import com.chronos.shiftservice.dto.shift.ShiftCardDTO;
import com.chronos.shiftservice.dto.shift.UpcomingShiftsRequestDTO;
import com.chronos.shiftservice.repository.ShiftRepository;
import com.chronos.shiftservice.repository.projections.EmployeeShiftView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/shifts/internal")
public class ShiftInternalController {
    private final ShiftRepository shiftRepository;

    public ShiftInternalController(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @PostMapping("/upcoming-by-employee-ids")
    public ResponseEntity<Map<UUID, List<ShiftCardDTO>>> getUpcomingByEmployeeIds(@RequestBody UpcomingShiftsRequestDTO request) {
        List<UUID> empIds = request.employeeIds() == null ? List.of() : request.employeeIds();
        if(empIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        var now = OffsetDateTime.now();
        List<EmployeeShiftView> views = shiftRepository.findUpcomingShiftViewByEmployeeIds(empIds, now);

        Map<UUID, List<ShiftCardDTO>> grouped = views.stream()
                .collect(Collectors.groupingBy(
                    EmployeeShiftView::getEmployeeId,
                    Collectors.mapping(v -> new ShiftCardDTO(
                            v.getId(),
                            v.getShiftId(),
                            v.getShiftDate(),
                            v.getShiftStartTime(),
                            v.getShiftEndTime(),
                            v.getShiftLocation(),
                            v.getShiftType(),
                            v.getShiftStatus()
                    ), Collectors.toList())
                ));

        return ResponseEntity.ok(grouped);

    }
}
