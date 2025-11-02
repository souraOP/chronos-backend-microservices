package com.chronos.reportservice.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReportResponseDTO(
        UUID id,
        String reportId,
        String teamId,
        LocalDate startDate,
        LocalDate endDate,
        Integer totalDaysPresent,
        Integer totalDaysAbsent,
        Double totalHoursWorked,
        Instant generatedAt
) {
}
