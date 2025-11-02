package com.chronos.leaveservice.controller;

import com.chronos.common.constants.enums.LeaveType;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceDTO;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO;
import com.chronos.leaveservice.service.impl.LeaveBalanceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "LeaveBalance CRUD Rest API",
        description = "REST APIs - Get Leave Balances by Employee, Create Leave Balance"
)

@RestController
@RequestMapping("/api/leave-balances")
@CrossOrigin("*")
public class LeaveBalanceController {
    private final LeaveBalanceServiceImpl leaveBalanceService;

    @Autowired
    public LeaveBalanceController(LeaveBalanceServiceImpl leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }



    @Operation(
            summary = "Get Leave Balances By Employee ID REST API",
            description = "Retrieve all leave balances for a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved leave balances",
                    content = @Content(schema = @Schema(implementation = LeaveBalanceResponseDTO.class)))
    })
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<List<LeaveBalanceResponseDTO>> getLeaveBalanceByEmployeeId(@PathVariable String employeeId) {
        List<LeaveBalanceResponseDTO> balances = leaveBalanceService.getLeaveBalancesByEmployeeId(employeeId);
        return new ResponseEntity<>(balances, HttpStatus.OK);
    }




    @Operation(
            summary = "Create Leave Balance REST API",
            description = "Create a new leave balance record for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created leave balance",
                    content = @Content(schema = @Schema(implementation = LeaveBalanceDTO.class)))
    })
    @PostMapping("/employees/{employeeId}")
    public ResponseEntity<LeaveBalanceDTO> createLeaveBalance(@PathVariable String employeeId, @RequestParam LeaveType leaveType, @RequestParam int leaveBalance) {
        LeaveBalanceDTO createdLeaveBalance = leaveBalanceService.createLeaveBalance(employeeId, leaveType, leaveBalance);
        return new ResponseEntity<>(createdLeaveBalance, HttpStatus.CREATED);
    }
}
