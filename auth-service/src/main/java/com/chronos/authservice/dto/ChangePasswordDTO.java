package com.chronos.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @NotNull
        @NotBlank(message = "password cannot be blank")
        @Size(min = 8, message = "Minimum password length is 8")
        String newPassword,

        @NotNull
        @NotBlank(message = "Please confirm your password and cannot be blank")
        String confirmPassword
) {
}
