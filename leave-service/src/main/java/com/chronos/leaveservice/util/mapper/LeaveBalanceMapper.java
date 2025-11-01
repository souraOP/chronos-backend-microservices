package com.chronos.leaveservice.util.mapper;


import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceDTO;
import com.chronos.leaveservice.entity.LeaveBalance;

public class LeaveBalanceMapper {
    private LeaveBalanceMapper() {
    }

    public static LeaveBalanceDTO leaveBalanceEntityToDTO(LeaveBalance leaveBalance) {
        if (leaveBalance == null) {
            return null;
        }

        return new LeaveBalanceDTO(
                leaveBalance.getId(),
                leaveBalance.getBalanceId(),
                leaveBalance.getLeaveType(),
                leaveBalance.getLeaveBalance()
        );
    }
}
