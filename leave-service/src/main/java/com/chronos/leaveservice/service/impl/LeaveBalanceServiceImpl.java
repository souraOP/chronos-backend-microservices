package com.chronos.leaveservice.service.impl;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.UuidErrorConstants;
import com.chronos.common.constants.enums.LeaveType;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.DuplicateLeaveBalanceFound;
import com.chronos.common.exception.custom.EmployeeNotFoundException;
import com.chronos.common.exception.custom.LeaveBalanceNotFoundException;
import com.chronos.common.util.NanoIdGenerator;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceDTO;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO;
import com.chronos.leaveservice.entity.LeaveBalance;
import com.chronos.leaveservice.feign.EmployeeClient;
import com.chronos.leaveservice.repository.LeaveBalanceRepository;
import com.chronos.leaveservice.util.mapper.LeaveBalanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.chronos.common.util.ParseUUID.parseUUID;

@Service
public class LeaveBalanceServiceImpl implements com.chronos.leaveservice.service.LeaveBalanceService {
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeClient employeeClient;

    @Autowired
    public LeaveBalanceServiceImpl(LeaveBalanceRepository leaveBalanceRepository, EmployeeClient employeeClient) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeClient = employeeClient;
    }

    @Override
    public List<LeaveBalanceResponseDTO> getLeaveBalancesByEmployeeId(String employeeId) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        return leaveBalanceRepository.findLeaveBalanceViewByEmployeeId(empID)
                .orElseThrow(() -> new LeaveBalanceNotFoundException(ErrorConstants.LEAVE_BALANCE_NOT_FOUND));
    }

    // for creating a leave balance
    @Override
    @Transactional
    public LeaveBalanceDTO createLeaveBalance(String employeeId, LeaveType leaveType, int leaveBalance) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        EmployeeDTO employee = employeeClient.getEmployeeById(employeeId);

        if(employee == null) {
            throw new EmployeeNotFoundException(ErrorConstants.EMPLOYEE_NOT_FOUND);
        }

        // for duplicate leave balance and leave type present in the database
        if (leaveBalanceRepository.existsByEmployeeIdAndLeaveType(empID, leaveType)) {
            throw new DuplicateLeaveBalanceFound(ErrorConstants.LEAVE_BALANCE_ALREADY_EXISTS);
        }

        LeaveBalance lb = new LeaveBalance();
        int balanceIdLength = 10;

        String nanoId = NanoIdUtils.randomNanoId(
                NanoIdGenerator.DEFAULT_NUMBER_GENERATOR,
                NanoIdGenerator.DEFAULT_ALPHABET,
                balanceIdLength
        );

        lb.setBalanceId("LB-" + nanoId);
        lb.setEmployeeId(empID);
        lb.setLeaveType(leaveType);
        lb.setLeaveBalance(leaveBalance);

        LeaveBalance savedLeaveBalance = leaveBalanceRepository.save(lb);

        return LeaveBalanceMapper.leaveBalanceEntityToDTO(savedLeaveBalance);
    }
}
