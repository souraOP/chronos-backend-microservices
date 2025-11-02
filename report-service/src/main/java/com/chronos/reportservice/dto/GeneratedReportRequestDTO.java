package com.chronos.reportservice.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GeneratedReportRequestDTO(
        @NotNull(message = "startDate is required")
        LocalDate startDate,
        @NotNull(message = "endDate is required")
        LocalDate endDate
) {
}
