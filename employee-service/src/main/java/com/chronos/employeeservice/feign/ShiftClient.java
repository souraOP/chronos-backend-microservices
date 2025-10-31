package com.chronos.employeeservice.feign;

import com.chronos.employeeservice.dto.ShiftCardDTO;
import com.chronos.employeeservice.dto.UpcomingShiftsRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "shift-service", path="/api/shifts/internal")
public interface ShiftClient {
    @PostMapping("/upcoming-by-employee-ids")
    Map<String, List<ShiftCardDTO>> getUpcomingByEmployeeIds(@RequestBody UpcomingShiftsRequestDTO request);
}
