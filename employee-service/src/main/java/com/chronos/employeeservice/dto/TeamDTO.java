package com.chronos.employeeservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record TeamDTO(
        @NotNull(message = "Please enter a valid teamId")
        String teamId,

        @NotNull(message = "Team name is required")
        @Size(min = 5, message = "Minimum name for team is 5 characters")
        String teamName,

        @NotNull(message = "Manager's ID is required")
        UUID teamManagersId,

        @NotNull(message = "Employee ID's are required")
        List<UUID> employeeIds
) {
}
