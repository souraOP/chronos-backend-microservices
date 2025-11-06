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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that manages employee leave balance operations.
 * <p>
 * Responsibilities:
 * - Retrieve leave balances for a specific employee.
 * - Create new leave balance records for employees.
 * <p>
 * Base path: /api/leave-balances
 * Security: Open endpoints for authorized users.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "LeaveBalance CRUD Rest API",
        description = "REST APIs - Get Leave Balances by Employee, Create Leave Balance"
)
@Slf4j
@RestController
@RequestMapping("/api/leave-balances")
public class LeaveBalanceController {
    private final LeaveBalanceServiceImpl leaveBalanceService;

    @Autowired
    public LeaveBalanceController(LeaveBalanceServiceImpl leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    /**
     * Retrieve all leave balances for a specific employee.
     * <p>
     * HTTP: GET /api/leave-balances/employees/{employeeId}
     * Security: Open endpoint.
     *
     * @param employeeId the unique identifier of the employee
     * @return list of all leave balances for the employee
     */

    @Operation(
            summary = "Get Leave Balances By Employee ID REST API",
            description = "Retrieve all leave balances for a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved leave balances",
                    content = @Content(schema = @Schema(implementation = LeaveBalanceResponseDTO.class))
            )
    })
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<List<LeaveBalanceResponseDTO>> getLeaveBalanceByEmployeeId(@PathVariable String employeeId) {
        log.info("Invoked the GET: getLeaveBalanceByEmployeeId controller method, employeeId:{}", employeeId);
        List<LeaveBalanceResponseDTO> balances = leaveBalanceService.getLeaveBalancesByEmployeeId(employeeId);
        return new ResponseEntity<>(balances, HttpStatus.OK);
    }

    /**
     * Create a new leave balance record for an employee.
     * <p>
     * HTTP: POST /api/leave-balances/employees/{employeeId}?leaveType={type}&leaveBalance={balance}
     * Security: Open endpoint.
     *
     * @param employeeId   the unique identifier of the employee
     * @param leaveType    the type of leave (e.g., SICK, CASUAL, ANNUAL)
     * @param leaveBalance the initial balance amount
     * @return the created leave balance record
     */

    @Operation(
            summary = "Create Leave Balance REST API",
            description = "Create a new leave balance record for an employee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully created leave balance",
                    content = @Content(schema = @Schema(implementation = LeaveBalanceDTO.class))
            )
    })
    @PostMapping("/employees/{employeeId}")
    public ResponseEntity<LeaveBalanceDTO> createLeaveBalance(@PathVariable String employeeId, @RequestParam LeaveType leaveType, @RequestParam int leaveBalance) {
        log.info("Invoked the POST: createLeaveBalance controller method, employeeId:{}, leaveType:{}, leaveBalance:{}", employeeId, leaveType, leaveBalance);
        LeaveBalanceDTO createdLeaveBalance = leaveBalanceService.createLeaveBalance(employeeId, leaveType, leaveBalance);
        return new ResponseEntity<>(createdLeaveBalance, HttpStatus.CREATED);
    }
}
