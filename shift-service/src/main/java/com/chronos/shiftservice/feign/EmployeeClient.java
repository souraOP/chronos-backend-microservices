package com.chronos.shiftservice.feign;

import com.chronos.shiftservice.dto.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "employee-service", path="/api")
public interface EmployeeClient {
    @GetMapping("/employees/{employeeId}")
    EmployeeDTO getEmployeeById(@PathVariable("employeeId") String employeeId);

    @GetMapping("/teams/manager/{managerId}/team-members")
    List<EmployeeDTO> getTeamMembers(@PathVariable String managerId);
}
