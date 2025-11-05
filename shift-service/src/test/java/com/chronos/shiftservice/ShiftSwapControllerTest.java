// language: java
package com.chronos.shiftservice;

import com.chronos.common.constants.enums.ShiftSwapRequestStatus;
import com.chronos.shiftservice.controller.ShiftSwapRequestController;
import com.chronos.shiftservice.dto.shiftSwapRequest.CreateShiftSwapRequestDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapQueryResponseDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapResponseDTO;
import com.chronos.shiftservice.service.impl.ShiftSwapRequestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ShiftSwapRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class ShiftSwapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShiftSwapRequestServiceImpl shiftSwapRequestService;

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
    @DisplayName("POST /api/shift-swap-requests/create -> returns 201 and response body")
    void createSwapRequest_ReturnsCreated() throws Exception {
        UUID id = UUID.randomUUID();
        UUID shiftId1 = UUID.randomUUID();
        UUID shiftId2 = UUID.randomUUID();

        ShiftSwapResponseDTO.ShiftInfo offering = new ShiftSwapResponseDTO.ShiftInfo(
                shiftId1, "MORNING", LocalDate.now().plusDays(1), OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(1).plusHours(8), "Office-A"
        );
        ShiftSwapResponseDTO.ShiftInfo requesting = new ShiftSwapResponseDTO.ShiftInfo(
                shiftId2, "EVENING", LocalDate.now().plusDays(2), OffsetDateTime.now().plusDays(2), OffsetDateTime.now().plusDays(2).plusHours(8), "Office-B"
        );

        ShiftSwapResponseDTO responseDto = new ShiftSwapResponseDTO(
                id,
                "SSR-XYZ123",
                "Alice",
                "Bob",
                ShiftSwapRequestStatus.PENDING,
                offering,
                requesting,
                "Can we swap?",
                null,
                null
        );

        Mockito.when(shiftSwapRequestService.createSwapRequest(org.mockito.ArgumentMatchers.any(CreateShiftSwapRequestDTO.class)))
                .thenReturn(responseDto);

        UUID requester = UUID.randomUUID();
        UUID requested = UUID.randomUUID();
        String reqJson = """
                {
                  "requesterEmployeeId": "%s",
                  "requestedEmployeeId": "%s",
                  "offeringShiftId": "%s",
                  "requestingShiftId": "%s",
                  "reason": "Can we swap?"
                }
                """.formatted(requester, requested, shiftId1, shiftId2);

        mockMvc.perform(post("/api/shift-swap-requests/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shiftSwapId", is("SSR-XYZ123")))
                .andExpect(jsonPath("$.fromEmployeeName", is("Alice")))
                .andExpect(jsonPath("$.toEmployeeName", is("Bob")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.reason", is("Can we swap?")));

        Mockito.verify(shiftSwapRequestService, times(1))
                .createSwapRequest(org.mockito.ArgumentMatchers.any(CreateShiftSwapRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/shift-swap-requests/employee/{employeeId} -> returns 200 and list")
    void getSwapRequestsForEmployee_ReturnsOk() throws Exception {
        UUID employeeId = UUID.randomUUID();
        ShiftSwapQueryResponseDTO dto = new ShiftSwapQueryResponseDTO(
                UUID.randomUUID(),
                "SSR-1",
                "Alice",
                "Bob",
                ShiftSwapRequestStatus.PENDING,
                "MORNING",
                LocalDate.now().plusDays(1),
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(8),
                "Office-A",
                "EVENING",
                LocalDate.now().plusDays(2),
                OffsetDateTime.now().plusDays(2),
                OffsetDateTime.now().plusDays(2).plusHours(8),
                "Office-B",
                "Reason",
                null,
                null
        );

        Mockito.when(shiftSwapRequestService.getSwapRequestsForEmployee(employeeId.toString()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/shift-swap-requests/employee/{employeeId}", employeeId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].shiftSwapId", is("SSR-1")))
                .andExpect(jsonPath("$[0].fromEmployeeName", is("Alice")))
                .andExpect(jsonPath("$[0].toEmployeeName", is("Bob")));

        Mockito.verify(shiftSwapRequestService, times(1)).getSwapRequestsForEmployee(employeeId.toString());
    }

    @Test
    @DisplayName("GET /api/shift-swap-requests/manager/{managerId}/requests -> returns 200 and list")
    void getTeamSwapRequests_ReturnsOk() throws Exception {
        String managerId = UUID.randomUUID().toString();

        ShiftSwapQueryResponseDTO dto = new ShiftSwapQueryResponseDTO(
                UUID.randomUUID(),
                "SSR-TEAM-1",
                "Carol",
                "Dave",
                ShiftSwapRequestStatus.PENDING,
                "MORNING",
                LocalDate.now().plusDays(3),
                OffsetDateTime.now().plusDays(3),
                OffsetDateTime.now().plusDays(3).plusHours(8),
                "Office-C",
                "EVENING",
                LocalDate.now().plusDays(4),
                OffsetDateTime.now().plusDays(4),
                OffsetDateTime.now().plusDays(4).plusHours(8),
                "Office-D",
                "Team reason",
                null,
                null
        );

        Mockito.when(shiftSwapRequestService.getTeamSwapRequests(managerId))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/shift-swap-requests/manager/{managerId}/requests", managerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].shiftSwapId", is("SSR-TEAM-1")))
                .andExpect(jsonPath("$[0].fromEmployeeName", is("Carol")));

        Mockito.verify(shiftSwapRequestService, times(1)).getTeamSwapRequests(managerId);
    }

    @Test
    @DisplayName("POST /api/shift-swap-requests/manager/{managerId}/requests/{swapRequestId}/approve -> returns 200")
    void approveSwapRequest_ReturnsOk() throws Exception {
        String managerId = UUID.randomUUID().toString();
        String swapReqId = UUID.randomUUID().toString();

        ShiftSwapResponseDTO.ShiftInfo offering = new ShiftSwapResponseDTO.ShiftInfo(
                UUID.randomUUID(), "MORNING", LocalDate.now().plusDays(1), OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(1).plusHours(8), "Office-A"
        );
        ShiftSwapResponseDTO.ShiftInfo requesting = new ShiftSwapResponseDTO.ShiftInfo(
                UUID.randomUUID(), "EVENING", LocalDate.now().plusDays(2), OffsetDateTime.now().plusDays(2), OffsetDateTime.now().plusDays(2).plusHours(8), "Office-B"
        );

        ShiftSwapResponseDTO resp = new ShiftSwapResponseDTO(
                UUID.randomUUID(),
                "SSR-APPROVED",
                "Alice",
                "Bob",
                ShiftSwapRequestStatus.APPROVED,
                offering,
                requesting,
                "Reason",
                "Manager X",
                OffsetDateTime.now()
        );

        Mockito.when(shiftSwapRequestService.approveSwapRequest(managerId, swapReqId))
                .thenReturn(resp);

        mockMvc.perform(post("/api/shift-swap-requests/manager/{managerId}/requests/{swapRequestId}/approve", managerId, swapReqId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shiftSwapId", is("SSR-APPROVED")))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.approvedByName", is("Manager X")));

        Mockito.verify(shiftSwapRequestService, times(1)).approveSwapRequest(managerId, swapReqId);
    }

    @Test
    @DisplayName("POST /api/shift-swap-requests/manager/{managerId}/requests/{swapRequestId}/reject -> returns 200")
    void rejectSwapRequest_ReturnsOk() throws Exception {
        String managerId = UUID.randomUUID().toString();
        String swapReqId = UUID.randomUUID().toString();

        ShiftSwapResponseDTO.ShiftInfo offering = new ShiftSwapResponseDTO.ShiftInfo(
                UUID.randomUUID(), "MORNING", LocalDate.now().plusDays(1), OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(1).plusHours(8), "Office-A"
        );
        ShiftSwapResponseDTO.ShiftInfo requesting = new ShiftSwapResponseDTO.ShiftInfo(
                UUID.randomUUID(), "EVENING", LocalDate.now().plusDays(2), OffsetDateTime.now().plusDays(2), OffsetDateTime.now().plusDays(2).plusHours(8), "Office-B"
        );

        ShiftSwapResponseDTO resp = new ShiftSwapResponseDTO(
                UUID.randomUUID(),
                "SSR-REJECTED",
                "Alice",
                "Bob",
                ShiftSwapRequestStatus.REJECTED,
                offering,
                requesting,
                "Reason",
                "Manager Y",
                OffsetDateTime.now()
        );

        Mockito.when(shiftSwapRequestService.rejectSwapRequest(managerId, swapReqId))
                .thenReturn(resp);

        mockMvc.perform(post("/api/shift-swap-requests/manager/{managerId}/requests/{swapRequestId}/reject", managerId, swapReqId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shiftSwapId", is("SSR-REJECTED")))
                .andExpect(jsonPath("$.status", is("REJECTED")))
                .andExpect(jsonPath("$.approvedByName", is("Manager Y")));

        Mockito.verify(shiftSwapRequestService, times(1)).rejectSwapRequest(managerId, swapReqId);
    }

}
