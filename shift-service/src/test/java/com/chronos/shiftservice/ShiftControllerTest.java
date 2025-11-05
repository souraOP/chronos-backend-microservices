package com.chronos.shiftservice;

import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.constants.enums.ShiftType;
import com.chronos.shiftservice.controller.ShiftController;
import com.chronos.shiftservice.dto.shift.CreateShiftDateRequestDTO;
import com.chronos.shiftservice.dto.shift.ShiftResponseDTO;
import com.chronos.shiftservice.dto.shift.TeamShiftTableRowDTO;
import com.chronos.shiftservice.service.impl.ShiftServiceImpl;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShiftController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShiftServiceImpl shiftService;

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
    void createShift_returnsCreatedAndBody() throws Exception {
        String managerId = "manager-1";
        UUID empId = UUID.randomUUID();

        LocalDate futureDate = LocalDate.now().plusDays(5);

        CreateShiftDateRequestDTO request = new CreateShiftDateRequestDTO(
                empId,
                futureDate,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                ShiftStatus.CONFIRMED,
                ShiftType.REGULAR,
                "Headquarters"
        );

        ZoneId zone = ZoneId.systemDefault();
        OffsetDateTime start = futureDate.atTime(9, 0).atZone(zone).toOffsetDateTime();
        OffsetDateTime end = futureDate.atTime(17, 0).atZone(zone).toOffsetDateTime();

        ShiftResponseDTO response = new ShiftResponseDTO(
                UUID.randomUUID(),
                "SH-ABC123",
                futureDate,
                start,
                end,
                ShiftStatus.CONFIRMED,
                ShiftType.REGULAR,
                "Headquarters"
        );

        when(shiftService.createShift(any(CreateShiftDateRequestDTO.class), eq(managerId))).thenReturn(response);

        mockMvc.perform(post("/api/shifts/manager/{managerId}/create", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shiftId").value("SH-ABC123"))
                .andExpect(jsonPath("$.shiftType").value("REGULAR"))
                .andExpect(jsonPath("$.shiftStatus").value("CONFIRMED"));
    }

    @Test
    void getEmployeeShifts_returnsOkAndList() throws Exception {
        UUID empId = UUID.randomUUID();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate shiftDate = LocalDate.now().plusDays(1);
        OffsetDateTime start = shiftDate.atTime(9, 0).atZone(zone).toOffsetDateTime();
        OffsetDateTime end = shiftDate.atTime(17, 0).atZone(zone).toOffsetDateTime();

        ShiftResponseDTO dto = new ShiftResponseDTO(
                UUID.randomUUID(),
                "SH-1",
                shiftDate,
                start,
                end,
                ShiftStatus.CONFIRMED,
                ShiftType.REGULAR,
                "Office Building"
        );

        when(shiftService.getEmployeeShifts(empId.toString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/shifts/{employeeId}", empId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].shiftId").value("SH-1"));
    }

    @Test
    void getTeamsShiftByManager_returnsOkAndList() throws Exception {
        String managerId = "manager-1";

        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = start.plusHours(8);

        ShiftResponseDTO a = new ShiftResponseDTO(
                UUID.randomUUID(),
                "SH-A",
                LocalDate.now(),
                start,
                end,
                ShiftStatus.CONFIRMED,
                ShiftType.REGULAR,
                "Location 1"
        );

        ShiftResponseDTO b = new ShiftResponseDTO(
                UUID.randomUUID(),
                "SH-B",
                LocalDate.now().plusDays(1),
                start,
                end,
                ShiftStatus.CONFIRMED,
                ShiftType.NIGHT,
                "Location 2"
        );

        when(shiftService.getTeamsShiftByManager(managerId)).thenReturn(List.of(a, b));

        mockMvc.perform(get("/api/shifts/manager/{managerId}/team-shifts", managerId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].shiftId").exists())
                .andExpect(jsonPath("$[1].shiftId").exists());
    }

    @Test
    void getTeamShiftsByManagerAndDatePicker_returnsOkAndRows() throws Exception {
        String managerId = "manager-1";
        LocalDate date = LocalDate.now().plusDays(5);

        ZoneId zone = ZoneId.systemDefault();
        OffsetDateTime start = date.atTime(9, 0).atZone(zone).toOffsetDateTime();
        OffsetDateTime end = date.atTime(17, 0).atZone(zone).toOffsetDateTime();

        TeamShiftTableRowDTO row = new TeamShiftTableRowDTO(
                UUID.randomUUID(),
                "SH-ROW",
                "Alice Smith",
                date,
                start,
                end,
                ShiftType.REGULAR,
                "Main Office",
                ShiftStatus.CONFIRMED
        );

        when(shiftService.getTeamShiftsByManagerAndDatePicker(managerId, date)).thenReturn(List.of(row));

        mockMvc.perform(get("/api/shifts/manager/{managerId}/team-shifts-by-date", managerId)
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].employeeName").value("Alice Smith"))
                .andExpect(jsonPath("$[0].shiftId").value("SH-ROW"));
    }
}
