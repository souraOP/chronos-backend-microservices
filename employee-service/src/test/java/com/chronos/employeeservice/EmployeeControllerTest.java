package com.chronos.employeeservice;

import com.chronos.common.constants.enums.Gender;
import com.chronos.common.constants.enums.Role;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.controller.EmployeeController;
import com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO;
import com.chronos.employeeservice.service.impl.EmployeeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeServiceImpl employeeService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @TestConfiguration
    static class NoopAuditorConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("test-user");
        }
    }

    @Test
    void createEmployee_returns201AndBody() throws Exception {
        UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        EmployeeDTO req = new EmployeeDTO(
                null, "EMP-1", "John", "Doe", "john@example.com",
                Gender.MALE, "111", "Dev", true, "Eng", Role.EMPLOYEE, "TEAM-1"
        );
        EmployeeDTO resp = new EmployeeDTO(
                id, "EMP-1", "John", "Doe", "john@example.com",
                Gender.MALE, "111", "Dev", true, "Eng", Role.EMPLOYEE, "TEAM-1"
        );

        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(employeeService).createEmployee(any(EmployeeDTO.class));
    }

    @Test
    void getEmployeeByDisplayId_returns200AndBody() throws Exception {
        EmployeeDTO dto = new EmployeeDTO(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "EMP-2", "Ishika", "Dutta", "ishika@example.com",
                Gender.FEMALE, "222", "QA", true, "QA", Role.EMPLOYEE, "TEAM-X"
        );

        when(employeeService.getEmployeeByDisplayId("EMP-2")).thenReturn(dto);

        mockMvc.perform(get("/api/employees/by-display-id/{displayEmployeeId}", "EMP-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayEmployeeId").value("EMP-2"))
                .andExpect(jsonPath("$.firstName").value("Ishika"));

        verify(employeeService).getEmployeeByDisplayId("EMP-2");
    }

    @Test
    void getEmployeeById_returns200AndBody() throws Exception {
        UUID id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        EmployeeDTO dto = new EmployeeDTO(
                id, "EMP-3", "Mani", "Kumar", "mani@example.com",
                Gender.MALE, "333", "Dev", true, "Eng", Role.EMPLOYEE, "[Not in a team]"
        );

        when(employeeService.getEmployeeById(id.toString())).thenReturn(dto);

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("Mani"));

        verify(employeeService).getEmployeeById(id.toString());
    }

    @Test
    void getEmployeeName_returns200AndBody() throws Exception {
        UUID id = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        EmployeeNameResponseDTO name = new EmployeeNameResponseDTO("John", "Doe");

        when(employeeService.getEmployeeName(id.toString())).thenReturn(name);

        mockMvc.perform(get("/api/employees/{id}/name", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(employeeService).getEmployeeName(id.toString());
    }

    @Test
    void getAllEmployees_returns200AndList() throws Exception {
        EmployeeDTO e1 = new EmployeeDTO(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "EMP-4", "Alice", "A", "alice@example.com",
                Gender.FEMALE, "444", "Dev", true, "Eng", Role.EMPLOYEE, "TEAM-1"
        );
        EmployeeDTO e2 = new EmployeeDTO(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "EMP-5", "Bob", "B", "bob@example.com",
                Gender.MALE, "555", "QA", true, "QA", Role.EMPLOYEE, "TEAM-2"
        );

        when(employeeService.getAllEmployees()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/employees/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void updateEmployee_returns201AndBody() throws Exception {
        UUID id = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        EmployeeDTO req = new EmployeeDTO(
                id, "EMP-6", "NewFirst", "NewLast", "new@example.com",
                Gender.MALE, "999", "Sr Dev", false, "NewDept", Role.MANAGER, "TEAM-9"
        );

        when(employeeService.updateEmployee(eq(id.toString()), any(EmployeeDTO.class))).thenReturn(req);

        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("NewFirst"))
                .andExpect(jsonPath("$.role").value("MANAGER"));

        verify(employeeService).updateEmployee(eq(id.toString()), any(EmployeeDTO.class));
    }

    @Test
    void patchEmployee_returns200AndBody() throws Exception {
        UUID id = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "Patched");
        updates.put("role", "MANAGER");

        EmployeeDTO patched = new EmployeeDTO(
                id, "EMP-7", "Patched", "Last", "patched@example.com",
                Gender.MALE, "000", "Dev", true, "Eng", Role.MANAGER, "TEAM-1"
        );

        when(employeeService.patchEmployee(eq(id.toString()), any(Map.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Patched"))
                .andExpect(jsonPath("$.role").value("MANAGER"));

        verify(employeeService).patchEmployee(eq(id.toString()), any(Map.class));
    }

    @Test
    void deleteEmployee_returns204() throws Exception {
        UUID id = UUID.fromString("99999999-9999-9999-9999-999999999999");

        mockMvc.perform(delete("/api/employees/{id}", id))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(id.toString());
    }
}

