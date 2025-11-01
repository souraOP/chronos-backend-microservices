package com.chronos.leaveservice.controller;

import com.chronos.common.constants.enums.LeaveType;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceDTO;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO;
import com.chronos.leaveservice.service.impl.LeaveBalanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balances")
@CrossOrigin("*")
public class LeaveBalanceController {
    private final LeaveBalanceServiceImpl leaveBalanceService;

    @Autowired
    public LeaveBalanceController(LeaveBalanceServiceImpl leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<List<LeaveBalanceResponseDTO>> getLeaveBalanceByEmployeeId(@PathVariable String employeeId) {
        List<LeaveBalanceResponseDTO> balances = leaveBalanceService.getLeaveBalancesByEmployeeId(employeeId);
        return new ResponseEntity<>(balances, HttpStatus.OK);
    }


    @PostMapping("/employees/{employeeId}")
    public ResponseEntity<LeaveBalanceDTO> createLeaveBalance(@PathVariable String employeeId, @RequestParam LeaveType leaveType, @RequestParam int leaveBalance) {
        LeaveBalanceDTO createdLeaveBalance = leaveBalanceService.createLeaveBalance(employeeId, leaveType, leaveBalance);
        return new ResponseEntity<>(createdLeaveBalance, HttpStatus.CREATED);
    }
}
