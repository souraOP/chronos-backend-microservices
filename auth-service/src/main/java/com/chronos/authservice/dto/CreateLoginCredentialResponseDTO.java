package com.chronos.authservice.dto;

public record CreateLoginCredentialResponseDTO(
        String loginCredentialId,

        String message,

        String employeeId
) {
}
