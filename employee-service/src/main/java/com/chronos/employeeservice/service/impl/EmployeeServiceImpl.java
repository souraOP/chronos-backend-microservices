package com.chronos.employeeservice.service.impl;


import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.UuidErrorConstants;
import com.chronos.common.constants.enums.Gender;
import com.chronos.common.constants.enums.Role;
import com.chronos.employeeservice.dto.employee.EmployeeDTO;
import com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO;
import com.chronos.employeeservice.entity.Employee;
import com.chronos.employeeservice.repository.EmployeeRepository;
import com.chronos.employeeservice.service.EmployeeService;
import com.chronos.employeeservice.util.mappers.EmployeeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.chronos.common.util.ParseUUID.parseUUID;


@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    // creating an employee
    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = EmployeeMapper.employeeDtoToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.employeeEntityToDto(savedEmployee);
    }

    // getting employee by their id
    @Override
    public EmployeeDTO getEmployeeById(String employeeID) {
        UUID empID = parseUUID(employeeID, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        return employeeRepository.findEmployeeByID(empID)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.EMP_FETCH_TERMINATED_NOT_FOUND + empID));
    }

    @Override
    public EmployeeDTO getEmployeeByDisplayId(String displayEmployeeId){
        Employee emp = employeeRepository.findByDisplayEmployeeId(displayEmployeeId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.EMP_FETCH_TERMINATED_NOT_FOUND));

        return EmployeeMapper.employeeEntityToDto(emp);
    }

    // getting all the employees
    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        for (Employee employee : employeeRepository.findAll()) {
            employeeDTOS.add(EmployeeMapper.employeeEntityToDto(employee));
        }
        return employeeDTOS;
    }

    // updating an employee
    @Override
    @Transactional
    public EmployeeDTO updateEmployee(String employeeID, EmployeeDTO employeeDTO) {
        UUID empID = parseUUID(employeeID, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        Employee employee = employeeRepository.findById(empID)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.EMP_UPDATE_TERMINATED_NOT_FOUND + empID));

        BeanUtils.copyProperties(employeeDTO, employee);

        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.employeeEntityToDto(updatedEmployee);
    }

    //patch employee
    @Override
    @Transactional
    public EmployeeDTO patchEmployee(String employeeId, Map<String, Object> updates) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);
        EmployeeDTO employee = employeeRepository.findEmployeeByID(empID)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.EMPLOYEE_NOT_FOUND));

        EmployeeDTO patchedEmployee = new EmployeeDTO(
                employee.id(),
                employee.displayEmployeeId(),
                (String) updates.getOrDefault("firstName", employee.firstName()),
                (String) updates.getOrDefault("lastName", employee.lastName()),
                (String) updates.getOrDefault("email", employee.email()),
                updates.containsKey("gender") ? Gender.valueOf(updates.get("gender").toString()) : employee.gender(),
                (String) updates.getOrDefault("phoneNumber", employee.phoneNumber()),
                (String) updates.getOrDefault("jobTitle", employee.jobTitle()),
                updates.containsKey("active") ? Boolean.parseBoolean(updates.get("active").toString()) : employee.isActive(),
                (String) updates.getOrDefault("departmentName", employee.departmentName()),
                updates.containsKey("role") ? Role.valueOf(updates.get("role").toString()) : employee.role(),
                (String) updates.getOrDefault("teamId", employee.teamId())
        );

        return updateEmployee(employeeId, patchedEmployee);
    }

    // deleting an employee
    @Override
    @Transactional
    public void deleteEmployee(String employeeId) {
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        if (!employeeRepository.existsById(empID)) {
            throw new RuntimeException(ErrorConstants.EMP_DELETE_TERMINATED_NOT_FOUND + empID);
        }
        employeeRepository.deleteById(empID);
    }


    @Override
    public EmployeeNameResponseDTO getEmployeeName(String employeeId) {
        try {
            UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

            employeeRepository.findById(empID)
                    .orElseThrow(() -> new RuntimeException(ErrorConstants.EMPLOYEE_NOT_FOUND));

            return employeeRepository.findEmployeeName(empID);
        } catch (RuntimeException e) {
            throw new RuntimeException(ErrorConstants.EMPLOYEE_NOT_FOUND);
        }
    }
}
