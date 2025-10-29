package com.chronos.employeeservice.service;

import com.chronos.employeeservice.dto.employee.EmployeeDTO;
import com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO getEmployeeById(String id);

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDetails);

    EmployeeDTO patchEmployee(String employeeId, Map<String, Object> updates);

    void deleteEmployee(String id);

    EmployeeNameResponseDTO getEmployeeName(String id);

    EmployeeDTO getEmployeeByDisplayId(String displayEmployeeId);
}
