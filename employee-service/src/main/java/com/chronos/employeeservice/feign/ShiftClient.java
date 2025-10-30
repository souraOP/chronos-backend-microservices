package com.chronos.employeeservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shift-service")
public interface ShiftClient {
}
