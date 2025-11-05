package com.chronos.attendanceservice;

import com.chronos.attendanceservice.controller.AttendanceController;
import com.chronos.attendanceservice.dto.AttendanceResponseDTO;
import com.chronos.attendanceservice.dto.CheckInRequestDTO;
import com.chronos.attendanceservice.dto.ManagerAttendanceDisplayByDateResponseDTO;
import com.chronos.attendanceservice.dto.ManagerAttendanceRowDTO;
import com.chronos.attendanceservice.service.impl.AttendanceServiceImpl;
import com.chronos.common.constants.enums.AttendanceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AttendanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttendanceServiceImpl attendanceService;

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
    @DisplayName("GET /api/attendances/{employeeId}/latest -> 200 and body")
    void getLatestAttendance_ReturnsOk() throws Exception {
        String employeeId = "11111111-1111-1111-1111-111111111111";

        AttendanceResponseDTO dto = new AttendanceResponseDTO(
                "ATT-123",
                LocalDate.of(2025, 1, 2),
                OffsetDateTime.parse("2025-01-02T09:00:00+00:00"),
                null,
                0.0,
                AttendanceStatus.ACTIVE,
                "Office-A"
        );

        Mockito.when(attendanceService.getLatestAttendance(employeeId)).thenReturn(dto);

        mockMvc.perform(get("/api/attendances/{employeeId}/latest", employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.attendanceId", is("ATT-123")))
                .andExpect(jsonPath("$.attendanceStatus", is("ACTIVE")))
                .andExpect(jsonPath("$.location", is("Office-A")));

        Mockito.verify(attendanceService, times(1)).getLatestAttendance(employeeId);
    }

    @Test
    @DisplayName("GET /api/attendances/{employeeId}/history -> 200 and list")
    void getAttendanceHistory_ReturnsOk() throws Exception {
        String employeeId = "22222222-2222-2222-2222-222222222222";

        AttendanceResponseDTO dto = new AttendanceResponseDTO(
                "ATT-H1",
                LocalDate.of(2025, 2, 1),
                OffsetDateTime.parse("2025-02-01T09:00:00+00:00"),
                OffsetDateTime.parse("2025-02-01T17:00:00+00:00"),
                8.0,
                AttendanceStatus.COMPLETE,
                "Office-B"
        );

        Mockito.when(attendanceService.getAttendanceHistory(employeeId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/attendances/{employeeId}/history", employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].attendanceId", is("ATT-H1")))
                .andExpect(jsonPath("$[0].hoursWorked", is(8.0)));

        Mockito.verify(attendanceService, times(1)).getAttendanceHistory(employeeId);
    }

    @Test
    @DisplayName("POST /api/attendances/{employeeId}/check-in -> 201 and body")
    void checkIn_ReturnsCreated() throws Exception {
        String employeeId = "33333333-3333-3333-3333-333333333333";

        CheckInRequestDTO req = new CheckInRequestDTO("Office-C");
        String reqJson = objectMapper.writeValueAsString(Map.of("location", req.location()));

        AttendanceResponseDTO resp = new AttendanceResponseDTO(
                "ATT-CI",
                LocalDate.of(2025, 3, 3),
                OffsetDateTime.parse("2025-03-03T09:30:00+00:00"),
                null,
                0.0,
                AttendanceStatus.ACTIVE,
                "Office-C"
        );

        Mockito.when(attendanceService.checkIn(Mockito.eq(employeeId),
                org.mockito.ArgumentMatchers.any())).thenReturn(resp);

        mockMvc.perform(post("/api/attendances/{employeeId}/check-in", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attendanceId", is("ATT-CI")))
                .andExpect(jsonPath("$.attendanceStatus", is("ACTIVE")))
                .andExpect(jsonPath("$.location", is("Office-C")));

        Mockito.verify(attendanceService, times(1))
                .checkIn(Mockito.eq(employeeId), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("POST /api/attendances/{employeeId}/check-out -> 201 and body")
    void checkOut_ReturnsCreated() throws Exception {
        String employeeId = "44444444-4444-4444-4444-444444444444";

        AttendanceResponseDTO resp = new AttendanceResponseDTO(
                "ATT-CO",
                LocalDate.of(2025, 4, 4),
                OffsetDateTime.parse("2025-04-04T08:00:00+00:00"),
                OffsetDateTime.parse("2025-04-04T17:00:00+00:00"),
                9.0,
                AttendanceStatus.COMPLETE,
                "Office-D"
        );

        Mockito.when(attendanceService.checkOut(employeeId)).thenReturn(resp);

        mockMvc.perform(post("/api/attendances/{employeeId}/check-out", employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attendanceId", is("ATT-CO")))
                .andExpect(jsonPath("$.attendanceStatus", is("COMPLETE")))
                .andExpect(jsonPath("$.hoursWorked", is(9.0)));

        Mockito.verify(attendanceService, times(1)).checkOut(employeeId);
    }

    @Test
    @DisplayName("GET /api/attendances/{managerId}/attendance?date= -> 200 and body")
    void getTeamAttendanceByDate_ReturnsOk() throws Exception {
        String managerId = "55555555-5555-5555-5555-555555555555";
        String date = "2025-05-05";

        ManagerAttendanceRowDTO row = new ManagerAttendanceRowDTO(
                "E-100",
                "Jane Smith",
                OffsetDateTime.parse("2025-05-05T09:00:00+00:00"),
                OffsetDateTime.parse("2025-05-05T17:00:00+00:00"),
                8.0,
                AttendanceStatus.COMPLETE
        );

        ManagerAttendanceDisplayByDateResponseDTO resp =
                new ManagerAttendanceDisplayByDateResponseDTO(LocalDate.parse(date), List.of(row));

        Mockito.when(attendanceService.getTeamsAttendanceByDate(managerId, date)).thenReturn(resp);

        mockMvc.perform(get("/api/attendances/{managerId}/attendance", managerId)
                        .param("date", date)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is(date)))
                // adjusted to match controller DTO JSON property name 'attendanceRows'
                .andExpect(jsonPath("$.attendanceRows", hasSize(1)))
                .andExpect(jsonPath("$.attendanceRows[0].displayEmployeeId", is("E-100")))
                // adjusted to match controller DTO JSON property name 'employeeName'
                .andExpect(jsonPath("$.attendanceRows[0].employeeName", containsString("Jane")));

        Mockito.verify(attendanceService, times(1)).getTeamsAttendanceByDate(managerId, date);
    }
}


