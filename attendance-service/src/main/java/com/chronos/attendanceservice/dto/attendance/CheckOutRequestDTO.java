package com.chronos.attendanceservice.dto.attendance;

import jakarta.validation.constraints.NotBlank;

public record CheckOutRequestDTO(
        @NotBlank
        String employeeId
) {
}
