package com.chronos.employeeservice.controller;

import com.chronos.employeeservice.dto.employee.EmployeeDTO;
import com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO;
import com.chronos.employeeservice.service.impl.EmployeeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/employees")
@CrossOrigin("*")
public class EmployeeController {
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }


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


    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable String id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<EmployeeNameResponseDTO> getEmployeeName(@PathVariable String id) {
        EmployeeNameResponseDTO getName = employeeService.getEmployeeName(id);
        return new ResponseEntity<>(getName, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> getEmployees = employeeService.getAllEmployees();
        return new ResponseEntity<>(getEmployees, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String id, @Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        return new ResponseEntity<>(updated, HttpStatus.CREATED);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDTO> patchEmployee(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        EmployeeDTO patchEmployee = employeeService.patchEmployee(id, updates);
        return new ResponseEntity<>(patchEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
