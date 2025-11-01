package com.chronos.shiftservice.service.impl;

import com.chronos.shiftservice.dto.shift.ShiftCardDTO;
import com.chronos.shiftservice.dto.shift.UpcomingShiftsRequestDTO;
import com.chronos.shiftservice.repository.ShiftRepository;
import com.chronos.shiftservice.repository.projections.EmployeeShiftView;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShiftServiceInternalImpl {

    private final ShiftRepository shiftRepository;

    public ShiftServiceInternalImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public Map<UUID, List<ShiftCardDTO>> getUpcomingShiftsByEmployeeIds(UpcomingShiftsRequestDTO request) {
        List<UUID> empIds = request.employeeIds() == null ? List.of() : request.employeeIds();
        if (empIds.isEmpty()) {
            return Collections.emptyMap();
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

        return grouped;
    }
}


