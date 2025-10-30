package com.chronos.authservice.dto;

import com.chronos.authservice.constants.enums.Role;
import jakarta.validation.constraints.Email;

public record GetAllLoginCredentialsDTO(
        String loginCredentialId,
        @Email String email,
        String passwordHash,
        String employeeId,
        Role role
) {
}
