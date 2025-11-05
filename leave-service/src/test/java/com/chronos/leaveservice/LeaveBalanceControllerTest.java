package com.chronos.leaveservice;

import com.chronos.common.constants.enums.LeaveType;
import com.chronos.leaveservice.controller.LeaveBalanceController;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceDTO;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO;
import com.chronos.leaveservice.service.impl.LeaveBalanceServiceImpl;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LeaveBalanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class LeaveBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveBalanceServiceImpl leaveBalanceService;

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
    @DisplayName("GET /api/leave-balances/employees/{employeeId} returns list")
    void getLeaveBalancesByEmployeeId_ReturnsList() throws Exception {
        String employeeId = "11111111-1111-1111-1111-111111111111";

        List<LeaveBalanceResponseDTO> balances = List.of(
                new LeaveBalanceResponseDTO("LB-001", LeaveType.SICK, 10),
                new LeaveBalanceResponseDTO("LB-002", LeaveType.VACATION, 20)
        );

        when(leaveBalanceService.getLeaveBalancesByEmployeeId(employeeId)).thenReturn(balances);

        mockMvc.perform(get("/api/leave-balances/employees/{employeeId}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].balanceId", is("LB-001")))
                .andExpect(jsonPath("$[0].leaveType", is("SICK")))
                .andExpect(jsonPath("$[0].leaveBalance", is(10)))
                .andExpect(jsonPath("$[1].balanceId", is("LB-002")))
                .andExpect(jsonPath("$[1].leaveType", is("VACATION")))
                .andExpect(jsonPath("$[1].leaveBalance", is(20)));

        verify(leaveBalanceService).getLeaveBalancesByEmployeeId(employeeId);
    }

    @Test
    @DisplayName("POST /api/leave-balances/employees/{employeeId} creates and returns 201")
    void createLeaveBalance_ReturnsCreated() throws Exception {
        String employeeId = "22222222-2222-2222-2222-222222222222";
        LeaveType leaveType = LeaveType.SICK;
        int leaveBalance = 15;

        UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        LeaveBalanceDTO dto = new LeaveBalanceDTO(id, "LB-XYZ", leaveType, leaveBalance);

        when(leaveBalanceService.createLeaveBalance(employeeId, leaveType, leaveBalance)).thenReturn(dto);

        mockMvc.perform(post("/api/leave-balances/employees/{employeeId}", employeeId)
                            .param("leaveType", leaveType.name())
                            .param("leaveBalance", String.valueOf(leaveBalance))
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.balanceId", is("LB-XYZ")))
                .andExpect(jsonPath("$.leaveType", is("SICK")))
                .andExpect(jsonPath("$.leaveBalance", is(15)));

        verify(leaveBalanceService).createLeaveBalance(employeeId, leaveType, leaveBalance);
    }
}