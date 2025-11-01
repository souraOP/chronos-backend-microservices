package com.chronos.shiftservice.dto;

import com.chronos.common.constants.enums.Gender;
import com.chronos.common.constants.enums.Role;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;

import java.util.UUID;


public record EmployeeDTO(

        UUID id,

        String displayEmployeeId,

        String firstName,

        String lastName,

        @Email(message = "Email is not correct")
        String email,

        Gender gender,

        String phoneNumber,

        String jobTitle,

        @JsonAlias({"active", "isActive"})
        boolean isActive,

        String departmentName,

        Role role,

        String teamId
) {
}
