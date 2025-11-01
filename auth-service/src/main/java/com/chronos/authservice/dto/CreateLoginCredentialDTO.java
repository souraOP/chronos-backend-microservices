package com.chronos.authservice.dto;

import com.chronos.common.constants.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLoginCredentialDTO(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password,

        @NotNull(message = "Employee ID cannot be blank")
        String employeeId,

        @NotNull(message = "Role is required")
        Role role
) {
}
