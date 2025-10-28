package com.chronos.authservice.dto;

import jakarta.validation.constraints.Email;

public record ChangePasswordResponseDTO(
        @Email String email,
        String message
) {
}
