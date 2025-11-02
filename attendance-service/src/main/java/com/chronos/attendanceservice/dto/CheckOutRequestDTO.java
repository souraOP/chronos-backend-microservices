package com.chronos.attendanceservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckOutRequestDTO(
        @NotBlank
        String employeeId
) {
}
