package com.chronos.shiftservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
@EnableDiscoveryClient
public class ShiftServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiftServiceApplication.class, args);
    }

}
