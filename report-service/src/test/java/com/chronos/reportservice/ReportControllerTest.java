package com.chronos.reportservice;

import com.chronos.reportservice.controller.ReportController;
import com.chronos.reportservice.dto.GeneratedReportRequestDTO;
import com.chronos.reportservice.dto.ReportResponseDTO;
import com.chronos.reportservice.service.ReportService;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportService reportService;

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
    void generate_returnsCreated() throws Exception {
        String managerId = "m1";
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 2);

        GeneratedReportRequestDTO req = new GeneratedReportRequestDTO(start, end);
        ReportResponseDTO resp = sampleDto("RPT-123", "T1");

        when(reportService.generatedReportForManager(eq(managerId), eq(start), eq(end))).thenReturn(resp);

        mockMvc.perform(post("/api/reports/manager/{managerId}/generate", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reportId").value("RPT-123"))
                .andExpect(jsonPath("$.teamId").value("T1"));
    }

    @Test
    void recentByManager_returnsOk() throws Exception {
        when(reportService.getRecentReportsForManager(eq("m1")))
                .thenReturn(List.of(sampleDto("RPT-1", "T1")));

        mockMvc.perform(get("/api/reports/manager/{managerId}/recent", "m1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reportId").value("RPT-1"));
    }

    @Test
    void recentByTeam_returnsOk() throws Exception {
        when(reportService.getRecentReportsForTeam(eq("T9")))
                .thenReturn(List.of(sampleDto("RPT-A", "T9"), sampleDto("RPT-B", "T9")));

        mockMvc.perform(get("/api/reports/team/{teamId}/recent", "T9"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].teamId").value("T9"))
                .andExpect(jsonPath("$[1].reportId").value("RPT-B"));
    }

    private static ReportResponseDTO sampleDto(String reportId, String teamId) {
        return new ReportResponseDTO(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                reportId,
                teamId,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                7,
                3,
                40.5,
                Instant.parse("2025-03-01T10:00:00Z")
        );
    }
}
