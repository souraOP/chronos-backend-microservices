package com.chronos.attendanceservice;

import com.chronos.attendanceservice.feign.EmployeeClient;
import com.chronos.attendanceservice.repository.AttendanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class AttendanceServiceApplicationTests {
    @MockitoBean
    private
    EmployeeClient employeeClient;

    @MockitoBean
    private AttendanceRepository shiftRepository;

    @Test
    void contextLoads() {
    }

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @TestConfiguration
    static class NoopAuditorConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("test-user");
        }
    }
}
