package com.chronos.authservice.feign;

import com.chronos.common.dto.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="employee-service")
public interface EmployeeClient {
    @GetMapping("/api/employees/by-display-id/{displayEmployeeId}")
    EmployeeDTO getEmployeeByDisplayId(@PathVariable String displayEmployeeId);
}
