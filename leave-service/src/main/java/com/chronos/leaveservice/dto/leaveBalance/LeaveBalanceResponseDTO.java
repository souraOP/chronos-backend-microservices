package com.chronos.leaveservice.dto.leaveBalance;

import com.chronos.common.constants.enums.LeaveType;

public record LeaveBalanceResponseDTO(
        String balanceId,
        LeaveType leaveType,
        int leaveBalance
) {
}
