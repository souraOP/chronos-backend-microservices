package com.chronos.reportservice.feign;

import com.chronos.common.dto.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "employee-service", path="/api/teams")
public interface EmployeeServiceClient {
    @GetMapping("/manager/{managerId}/team-members")
    List<EmployeeDTO> getTeamMembers(@PathVariable("managerId") String managerId);
}
