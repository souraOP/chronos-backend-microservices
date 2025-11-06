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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller that manages employee lifecycle and information operations.
 * <p>
 * Responsibilities:
 * - Create new employee records in the system.
 * - Retrieve employee information by ID or display ID.
 * - Retrieve employee name details.
 * - List all employees in the system.
 * - Update employee information (full and partial updates).
 * - Delete employee records from the system.
 * <p>
 * Base path: /api/employees
 * Security: Endpoints support role-based access as required.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "Employee CRUD Rest API",
        description = "CRUD Rest APIs - Create Employee, Update Employee, Get All Employee, Get Employee By Id, Update Employee, Delete Employee, Patch Employee"
)
@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Create a new employee record in the system.
     * <p>
     * HTTP: POST /api/employees
     * Security: Open endpoint.
     * <p>
     * This endpoint validates the employee data and persists it to the database.
     * All required fields must be provided and pass validation rules.
     *
     * @param employeeDTO the employee data transfer object containing all employee details
     * @return ResponseEntity containing the created employee with generated ID and HTTP 201 status
     */

    @Operation(
            summary = "Create Employee REST API",
            description = "Create Employee REST API endpoint is used to save an employee into the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Http Status 201 Created",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - validation failed or malformed request"
            )
    })
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("Invoked the POST: createEmployee controller method, employeeDTO:{}", employeeDTO);
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return new ResponseEntity<>(createdEmployee,HttpStatus.CREATED);
    }

    /**
     * Retrieve an employee by their display ID.
     * <p>
     * HTTP: GET /api/employees/by-display-id/{displayEmployeeId}
     * Security: Open endpoint.
     * <p>
     * The display ID is a human-readable identifier (e.g., "EMP001") as opposed to
     * the system-generated UUID. This method is useful for lookups using employee badges
     * or printed employee IDs.
     *
     * @param displayEmployeeId the display employee ID (human-readable identifier)
     * @return ResponseEntity containing the employee details and HTTP 200 status
     */

    @GetMapping("/by-display-id/{displayEmployeeId}")
    public ResponseEntity<EmployeeDTO> getEmployeeByDisplayId(@PathVariable String displayEmployeeId){
        log.info("Invoked the GET: getEmployeeByDisplayId controller method, displayEmployeeId:{}", displayEmployeeId);
        EmployeeDTO employee = employeeService.getEmployeeByDisplayId(displayEmployeeId);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    /**
     * Retrieve an employee by their unique employee ID (UUID).
     * <p>
     * HTTP: GET /api/employees/{employeeId}
     * Security: Open endpoint.
     * <p>
     * Fetches a single employee record from the database using the system-generated UUID.
     * This is the primary method for retrieving employee details by their unique identifier.
     *
     * @param employeeId the unique identifier (UUID) of the employee
     * @return ResponseEntity containing the employee details and HTTP 200 status
     */

    @Operation(
            summary = "Get Employee By ID REST API",
            description = "Get Employee By ID REST API endpoint is used to fetch a single employee from the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - invalid id supplied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - employee with given id does not exist"
            )
    })
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("employeeId") String employeeId) {
        log.info("Invoked the GET: getEmployeeId controller method, employeeId:{}", employeeId);
        EmployeeDTO employee = employeeService.getEmployeeById(employeeId);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    /**
     * Retrieve the first name and last name of an employee.
     * <p>
     * HTTP: GET /api/employees/{employeeId}/name
     * Security: Open endpoint.
     * <p>
     * This lightweight endpoint returns only the employee's name information,
     * which is useful for display purposes where full employee details are not needed.
     *
     * @param employeeId the unique identifier (UUID) of the employee
     * @return ResponseEntity containing the employee's name details and HTTP 200 status
     */

    @Operation(
            summary = "Get Employee Name REST API",
            description = "Get Employee Name REST API endpoint is used to fetch the employee First Name and Last Name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeNameResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - invalid id supplied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - employee with given id does not exist"
            )
    })
    @GetMapping("/{employeeId}/name")
    public ResponseEntity<EmployeeNameResponseDTO> getEmployeeName(@PathVariable("employeeId") String employeeId) {
        log.info("Invoked GET: getEmployeeName controller method, employeeId:{}", employeeId);
        EmployeeNameResponseDTO getName = employeeService.getEmployeeName(employeeId);
        return new ResponseEntity<>(getName, HttpStatus.OK);
    }

    /**
     * Retrieve all employees from the system.
     * <p>
     * HTTP: GET /api/employees/all
     * Security: Open endpoint.
     * <p>
     * Returns a complete list of all employee records in the database.
     * This endpoint may return large datasets and should be used carefully in production.
     * Consider using pagination for large employee bases.
     *
     * @return ResponseEntity containing list of all employees and HTTP 200 status
     */

    @Operation(
            summary = "Get All Employees REST API",
            description = "Get All Employees REST API endpoint is used to fetch all employees from the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO[].class))
            )
    })
    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.info("Invoked the GET: getAllEmployees controller method");
        List<EmployeeDTO> getEmployees = employeeService.getAllEmployees();
        return new ResponseEntity<>(getEmployees, HttpStatus.OK);
    }

    /**
     * Update an existing employee's details (full update).
     * <p>
     * HTTP: PUT /api/employees/{id}
     * Security: Open endpoint.
     * <p>
     * This endpoint performs a complete replacement of the employee record.
     * All fields in the request body will replace the existing values.
     * Use PATCH endpoint for partial updates.
     *
     * @param employeeId  the unique identifier (UUID) of the employee to update
     * @param employeeDTO the complete updated employee data
     * @return ResponseEntity containing the updated employee details and HTTP 201 status
     */

    @Operation(
            summary = "Update Employee REST API",
            description = "Update Employee REST API endpoint is used to update an existing employee's details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable("id") String employeeId, @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("Invoked the PUT: updateEmployee controller method, employeeId:{}, employeeDTO:{}", employeeId, employeeDTO);
        EmployeeDTO updated = employeeService.updateEmployee(employeeId, employeeDTO);
        return new ResponseEntity<>(updated, HttpStatus.CREATED);
    }

    /**
     * Perform a partial update on an employee's details.
     * <p>
     * HTTP: PATCH /api/employees/{id}
     * Security: Open endpoint.
     * <p>
     * This endpoint allows updating specific fields without replacing the entire record.
     * Only the fields present in the updates map will be modified; other fields remain unchanged.
     * This is useful for updating individual properties like email, phone, or department.
     *
     * @param employeeId the unique identifier (UUID) of the employee to update
     * @param updates    map of field names to updated values (only specified fields are updated)
     * @return ResponseEntity containing the partially updated employee details and HTTP 200 status
     */

    @Operation(
            summary = "Patch Employee REST API",
            description = "Patch Employee REST API endpoint is used to perform a partial update on an employee's details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status 200 Success",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO.class))
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDTO> patchEmployee(@PathVariable("id") String employeeId, @RequestBody Map<String, Object> updates) {
        log.info("Invoked the PATCH: patchEmployee controller method, employeeId:{}, updates:{}", employeeId, updates);
        EmployeeDTO patchEmployee = employeeService.patchEmployee(employeeId, updates);
        return new ResponseEntity<>(patchEmployee, HttpStatus.OK);
    }

    /**
     * Remove an employee from the system.
     * <p>
     * HTTP: DELETE /api/employees/{id}
     * Security: Open endpoint.
     * <p>
     * This endpoint permanently deletes an employee record from the database.
     * This action cannot be undone. Consider implementing soft delete for production use.
     * All related data (attendance, leaves, shifts) should be handled appropriately.
     *
     * @param employeeId the unique identifier (UUID) of the employee to delete
     * @return ResponseEntity with no content and HTTP 204 status
     */

    @Operation(
            summary = "Delete Employee REST API",
            description = "Delete Employee REST API endpoint is used to remove an employee from the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Http Status 204 No Content"
            )
    })
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("employeeId") String employeeId) {
        log.info("Invoked the DELETE: deleteEmployee controller method, employeeId:{}", employeeId);
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }
}
