package com.chronos.reportservice.feign;

import com.chronos.reportservice.dto.AttendanceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "attendance-service", path = "/api/attendances")
public interface AttendanceServiceClient {
    @GetMapping("/{employeeId}/history")
    List<AttendanceResponseDTO> getAttendanceHistory(@PathVariable("employeeId") String employeeId);
}
