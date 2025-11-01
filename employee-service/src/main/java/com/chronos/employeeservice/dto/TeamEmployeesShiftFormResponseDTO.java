package com.chronos.employeeservice.dto;

import java.util.UUID;

public record TeamEmployeesShiftFormResponseDTO(
        UUID id,
        String firstName,
        String lastName
) {
}
