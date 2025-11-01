package com.chronos.leaveservice.service;


import com.chronos.common.constants.enums.LeaveType;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceDTO;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO;

import java.util.List;

public interface LeaveBalanceService {
    List<LeaveBalanceResponseDTO> getLeaveBalancesByEmployeeId(String employeeId);

    LeaveBalanceDTO createLeaveBalance(String employeeId, LeaveType leaveType, int leaveBalance);
}
