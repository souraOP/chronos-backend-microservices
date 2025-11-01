package com.chronos.shiftservice.dto.shift;

import java.util.UUID;
import java.util.List;

public record UpcomingShiftsRequestDTO(
        List<UUID> employeeIds
) {
}
