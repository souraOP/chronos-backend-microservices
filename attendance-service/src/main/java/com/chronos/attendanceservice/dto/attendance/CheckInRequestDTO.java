package com.chronos.attendanceservice.dto.attendance;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequestDTO(
    @NotBlank(message = "Location is required")
    String location
) {
}
