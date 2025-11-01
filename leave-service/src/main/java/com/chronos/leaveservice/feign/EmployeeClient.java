package com.chronos.leaveservice.feign;

import com.chronos.leaveservice.dto.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="employee-service", path="/api")
public interface EmployeeClient {
    @GetMapping("/employees/{id}")
    EmployeeDTO getEmployeeById(@PathVariable("id") String id);

    @GetMapping("/teams/manager/{managerId}/team-members")
    List<EmployeeDTO> getTeamMembers(@PathVariable("managerId") String managerId);
}
