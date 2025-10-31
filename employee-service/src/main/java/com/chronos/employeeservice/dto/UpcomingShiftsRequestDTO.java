package com.chronos.employeeservice.dto;

import java.util.List;
import java.util.UUID;

public record UpcomingShiftsRequestDTO(
        List<UUID> employeeIds
) {
}
