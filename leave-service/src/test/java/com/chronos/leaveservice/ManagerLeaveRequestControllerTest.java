package com.chronos.leaveservice;

import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.constants.enums.LeaveType;
import com.chronos.leaveservice.controller.ManagerLeaveRequestController;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDataDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ManagerLeaveRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class ManagerLeaveRequestControllerTest {

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
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /api/leave-requests/manager/{managerId} returns list")
    void getTeamLeaveRequests_ReturnsList() throws Exception {
        String managerId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        ManagerLeaveRequestDTO r1 = new ManagerLeaveRequestDTO(
                UUID.fromString("10000000-0000-0000-0000-000000000001"),
                UUID.fromString("20000000-0000-0000-0000-000000000002"),
                "EMP-001",
                "Jane",
                "Doe",
                LeaveType.SICK,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 12),
                3,
                LeaveStatus.PENDING,
                "Flu"
        );

        when(leaveRequestService.getTeamLeaveRequests(managerId))
                .thenReturn(List.of(r1));

        mockMvc.perform(get("/api/leave-requests/manager/{managerId}", managerId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(leaveRequestService).getTeamLeaveRequests(managerId);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /api/leave-requests/manager/{managerId}/dashboard returns list")
    void getManagerDashboard_ReturnsList() throws Exception {
        String managerId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";

        ManagerLeaveRequestDashboardResponseDTO d1 =
                new ManagerLeaveRequestDashboardResponseDTO(
                        "LR-ABC123",
                        "John Smith",
                        LeaveType.VACATION,
                        LocalDate.of(2025, 2, 1),
                        LocalDate.of(2025, 2, 5)
                );

        when(leaveRequestService.getLeaveRequestManagerDashboard(managerId))
                .thenReturn(List.of(d1));

        mockMvc.perform(get("/api/leave-requests/manager/{managerId}/dashboard", managerId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].leaveRequestId", is("LR-ABC123")))
                .andExpect(jsonPath("$[0].leaveType", is("VACATION")));

        verify(leaveRequestService).getLeaveRequestManagerDashboard(managerId);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /api/leave-requests/manager/{managerId}/stats returns counts")
    void getManagerStats_ReturnsData() throws Exception {
        String managerId = "cccccccc-cccc-cccc-cccc-cccccccccccc";

        ManagerLeaveRequestDataDTO stats = new ManagerLeaveRequestDataDTO(
                2, 5, 1, 3
        );

        when(leaveRequestService.getLeaveRequestsStatsByManager(managerId))
                .thenReturn(stats);

        mockMvc.perform(get("/api/leave-requests/manager/{managerId}/stats", managerId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pending", is(2)))
                .andExpect(jsonPath("$.approved", is(5)))
                .andExpect(jsonPath("$.rejected", is(1)))
                .andExpect(jsonPath("$.onLeaveToday", is(3)));

        verify(leaveRequestService).getLeaveRequestsStatsByManager(managerId);
    }
}