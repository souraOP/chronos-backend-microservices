package com.chronos.leaveservice;

import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.constants.enums.LeaveType;
import com.chronos.leaveservice.controller.LeaveController;
import com.chronos.leaveservice.dto.leaveRequests.EmployeeLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestCreateRequestDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestResponseDTO;
import com.chronos.leaveservice.service.impl.LeaveRequestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LeaveController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class LeaveRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveRequestServiceImpl leaveRequestService;

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
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("GET /api/leave-requests/employees/{employeeId} returns list")
    void getEmployeeLeaveRequests_ReturnsList() throws Exception {
        String employeeId = "11111111-1111-1111-1111-111111111111";

        LeaveRequestResponseDTO r1 = new LeaveRequestResponseDTO(
                "LR-AAA",
                LeaveType.SICK,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 12),
                3,
                LeaveStatus.PENDING,
                OffsetDateTime.now(),
                "Sick leave"
        );
        LeaveRequestResponseDTO r2 = new LeaveRequestResponseDTO(
                "LR-BBB",
                LeaveType.VACATION,
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 5),
                5,
                LeaveStatus.APPROVED,
                OffsetDateTime.now(),
                "Family trip"
        );

        when(leaveRequestService.getEmployeeLeaveRequests(employeeId))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/leave-requests/employees/{employeeId}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].leaveRequestId", is("LR-AAA")))
                .andExpect(jsonPath("$[0].leaveType", is("SICK")))
                .andExpect(jsonPath("$[1].leaveRequestId", is("LR-BBB")))
                .andExpect(jsonPath("$[1].leaveType", is("VACATION")));

        verify(leaveRequestService).getEmployeeLeaveRequests(employeeId);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("POST /api/leave-requests/employees/{employeeId} creates a leave request and returns 201")
    void createLeaveRequest_ReturnsCreated() throws Exception {
        String employeeId = "22222222-2222-2222-2222-222222222222";

        LeaveRequestResponseDTO created = new LeaveRequestResponseDTO(
                "LR-XYZ",
                LeaveType.SICK,
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 12),
                3,
                LeaveStatus.PENDING,
                OffsetDateTime.now(),
                "Flu and rest"
        );

        when(leaveRequestService.createLeaveRequest(eq(employeeId), any(LeaveRequestCreateRequestDTO.class)))
                .thenReturn(created);

        String body = """
                {
                  "leaveType": "SICK",
                  "startDate": "2025-03-10",
                  "endDate": "2025-03-12",
                  "reason": "Flu and rest"
                }
                """;

        mockMvc.perform(post("/api/leave-requests/employees/{employeeId}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.leaveRequestId", is("LR-XYZ")))
                .andExpect(jsonPath("$.leaveType", is("SICK")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(leaveRequestService).createLeaveRequest(eq(employeeId), any(LeaveRequestCreateRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("GET /api/leave-requests/employees/{employeeId}/dashboard returns list")
    void getEmployeeDashboard_ReturnsList() throws Exception {
        String employeeId = "33333333-3333-3333-3333-333333333333";

        EmployeeLeaveRequestDashboardResponseDTO d1 =
                new EmployeeLeaveRequestDashboardResponseDTO(
                        "LR-123",
                        LeaveType.PERSONAL,
                        LocalDate.of(2025, 4, 1),
                        LocalDate.of(2025, 4, 1),
                        LeaveStatus.REJECTED
                );

        when(leaveRequestService.getLeaveRequestEmployeeDashboard(employeeId))
                .thenReturn(List.of(d1));

        mockMvc.perform(get("/api/leave-requests/employees/{employeeId}/dashboard", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].leaveRequestId", is("LR-123")))
                .andExpect(jsonPath("$[0].leaveType", is("PERSONAL")))
                .andExpect(jsonPath("$[0].status", is("REJECTED")));

        verify(leaveRequestService).getLeaveRequestEmployeeDashboard(employeeId);
    }
}