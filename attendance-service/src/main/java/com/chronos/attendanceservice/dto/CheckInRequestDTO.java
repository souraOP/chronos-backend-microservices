package com.chronos.attendanceservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequestDTO(
    @NotBlank(message = "Location is required")
    String location
) {
}
