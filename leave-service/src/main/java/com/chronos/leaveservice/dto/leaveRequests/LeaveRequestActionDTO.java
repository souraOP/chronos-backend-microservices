package com.chronos.leaveservice.dto.leaveRequests;

import com.chronos.common.constants.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;


public record LeaveRequestActionDTO(
        @NotNull(message = "Please enter a valid action [APPROVED | REJECTED]")
        LeaveStatus action
) {
}
