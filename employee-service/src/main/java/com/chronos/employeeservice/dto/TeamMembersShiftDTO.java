package com.chronos.employeeservice.dto;

import java.util.List;
import java.util.UUID;

public record TeamMembersShiftDTO(
        UUID id,
        String firstName,
        String lastName,
        List<ShiftCardDTO> shifts
) {
}
