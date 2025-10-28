package com.chronos.authservice.dto;

import jakarta.validation.constraints.Email;

import java.util.UUID;

public record EmployeeDTO(
        UUID id,
        String displayEmployeeId,
        String firstName,
        String lastName,

        @Email(message = "Email is not correct")
        String email
) {
}
