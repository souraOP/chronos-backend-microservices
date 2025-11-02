package com.chronos.employeeservice.util.mappers;

import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.entity.Employee;

public class EmployeeMapper {
    public static EmployeeDTO employeeEntityToDto(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getDisplayEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getGender(),
                employee.getPhoneNumber(),
                employee.getJobTitle(),
                employee.isActive(),
                employee.getDepartmentName(),
                employee.getRole(),
                employee.getTeam() != null ? employee.getTeam().getTeamId() : null
        );
    }

    public static Employee employeeDtoToEntity(EmployeeDTO empDto) {
        Employee employee = new Employee();
        employee.setId(empDto.id());
        employee.setDisplayEmployeeId((empDto.displayEmployeeId()));
        employee.setFirstName(empDto.firstName());
        employee.setLastName(empDto.lastName());
        employee.setEmail(empDto.email());
        employee.setGender(empDto.gender());
        employee.setPhoneNumber(empDto.phoneNumber());
        employee.setJobTitle(empDto.jobTitle());
        employee.setActive(empDto.isActive());
        employee.setDepartmentName(empDto.departmentName());
        employee.setRole(empDto.role());

        return employee;
    }
}