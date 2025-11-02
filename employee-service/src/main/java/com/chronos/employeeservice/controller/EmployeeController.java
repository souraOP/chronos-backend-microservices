package com.chronos.employeeservice.controller;

import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO;
import com.chronos.employeeservice.service.impl.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(
        name = "Employee CRUD Rest API",
        description = "CRUD Rest APIs - Create Employee, Update Employee, Get All Employee, Get Employee By Id, Update Employee, Delete Employee, Patch Employee"
)
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }



    @Operation(
            summary = "Create Employee REST API",
            description = "Create Employee REST API endpoint is used to save an employee into the database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Http Status 201 Created",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - validation failed or malformed request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return new ResponseEntity<>(createdEmployee,HttpStatus.CREATED);
    }





    @GetMapping("/by-display-id/{displayEmployeeId}")
    public ResponseEntity<EmployeeDTO> getEmployeeByDisplayId(@PathVariable String displayEmployeeId){
        EmployeeDTO employee = employeeService.getEmployeeByDisplayId(displayEmployeeId);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }



    @Operation(
            summary = "Get Employee By ID REST API",
            description = "Get Employee By ID REST API endpoint is used to fetch a single employee from the database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Not Found - employee with given id does not exist"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable String id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }



    @Operation(
            summary = "Get Employee Name REST API",
            description = "Get Employee Name REST API endpoint is used to fetch the employee First Name and Last Name"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeNameResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Not Found - employee with given id does not exist")
    })
    @GetMapping("/{id}/name")
    public ResponseEntity<EmployeeNameResponseDTO> getEmployeeName(@PathVariable String id) {
        EmployeeNameResponseDTO getName = employeeService.getEmployeeName(id);
        return new ResponseEntity<>(getName, HttpStatus.OK);
    }



    @Operation(
            summary = "Get All Employees REST API",
            description = "Get All Employees REST API endpoint is used to fetch all employees from the database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO[].class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> getEmployees = employeeService.getAllEmployees();
        return new ResponseEntity<>(getEmployees, HttpStatus.OK);
    }



    @Operation(
            summary = "Update Employee REST API",
            description = "Update Employee REST API endpoint is used to update an existing employee's details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String id, @Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        return new ResponseEntity<>(updated, HttpStatus.CREATED);
    }



    @Operation(
            summary = "Patch Employee REST API",
            description = "Patch Employee REST API endpoint is used to perform a partial update on an employee's details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDTO> patchEmployee(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        EmployeeDTO patchEmployee = employeeService.patchEmployee(id, updates);
        return new ResponseEntity<>(patchEmployee, HttpStatus.OK);
    }



    @Operation(
            summary = "Delete Employee REST API",
            description = "Delete Employee REST API endpoint is used to remove an employee from the database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Http Status 204 No Content")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
