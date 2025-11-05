package com.chronos.employeeservice;

import com.chronos.employeeservice.repository.EmployeeRepository;
import com.chronos.employeeservice.repository.TeamRepository;
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
class EmployeeServiceApplicationTests {

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private TeamRepository teamRepository;


    @Test
    void contextLoads() {
    }

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @TestConfiguration
    static class NoopAuditorConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("test-user");
        }
    }

}
