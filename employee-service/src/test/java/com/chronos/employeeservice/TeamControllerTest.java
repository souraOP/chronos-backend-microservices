package com.chronos.employeeservice;

import com.chronos.common.constants.enums.Gender;
import com.chronos.common.constants.enums.Role;
import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.constants.enums.ShiftType;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.controller.TeamController;
import com.chronos.employeeservice.dto.ShiftCardDTO;
import com.chronos.employeeservice.dto.TeamDTO;
import com.chronos.employeeservice.dto.TeamEmployeesShiftFormResponseDTO;
import com.chronos.employeeservice.dto.TeamMembersShiftDTO;
import com.chronos.employeeservice.service.impl.TeamServiceImpl;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class TeamControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TeamServiceImpl teamService;

    @MockitoBean(name = "jpaMappingContext")
    JpaMetamodelMappingContext jpaMappingContext;

    @TestConfiguration
    static class NoopAuditorConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("test-user");
        }
    }

    @Test
    void createTeam_returns201AndBody() throws Exception {
        UUID managerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID emp1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID emp2 = UUID.fromString("22222222-2222-2222-2222-222222222222");

        TeamDTO req = new TeamDTO("TEAM-1", "Alpha Team", managerId, List.of(emp1, emp2));
        when(teamService.createTeam(any(TeamDTO.class))).thenReturn(req);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value("TEAM-1"))
                .andExpect(jsonPath("$.teamName").value("Alpha Team"));

        verify(teamService).createTeam(any(TeamDTO.class));
    }

    @Test
    void getTeamSize_returns200() throws Exception {
        String managerId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
        when(teamService.getTeamSize(managerId)).thenReturn(3);

        mockMvc.perform(get("/api/teams/manager/{managerId}/teamSize", managerId))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(teamService).getTeamSize(managerId);
    }


    @Test
    void getTeamMembers_returns200AndList() throws Exception {
        String managerId = "dddddddd-dddd-dddd-dddd-dddddddddddd";
        EmployeeDTO e1 = new EmployeeDTO(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "EMP-10", "Alice", "A", "alice@example.com",
                Gender.FEMALE, "111", "Dev", true, "Eng", Role.EMPLOYEE, "TEAM-1"
        );
        EmployeeDTO e2 = new EmployeeDTO(
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                "EMP-11", "Bob", "B", "bob@example.com",
                Gender.MALE, "222", "QA", true, "QA", Role.EMPLOYEE, "TEAM-1"
        );

        when(teamService.getTeamMembers(managerId)).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/teams/manager/{managerId}/team-members", managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));

        verify(teamService).getTeamMembers(managerId);
    }

    @Test
    void getTeamMembersWithUpcomingShifts_returns200AndList() throws Exception {
        String employeeId = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";
        UUID m1 = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID m2 = UUID.fromString("66666666-6666-6666-6666-666666666666");

        ShiftCardDTO shift = new ShiftCardDTO(
                UUID.fromString("77777777-7777-7777-7777-777777777777"),
                "SHIFT-001",
                LocalDate.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0),
                OffsetDateTime.now().plusDays(1).withHour(17).withMinute(0).withSecond(0).withNano(0),
                "HQ",
                ShiftType.LATE,
                ShiftStatus.CONFIRMED
        );

        TeamMembersShiftDTO t1 = new TeamMembersShiftDTO(m1, "Sourasish", "Mondal", List.of(shift));
        TeamMembersShiftDTO t2 = new TeamMembersShiftDTO(m2, "Tom", "Jerry", List.of());

        when(teamService.getTeamMembersWithUpcomingShifts(employeeId)).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/teams/{employeeId}/members-with-upcoming-shifts", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Sourasish"))
                .andExpect(jsonPath("$[0].shifts", hasSize(1)))
                .andExpect(jsonPath("$[1].firstName").value("Tom"))
                .andExpect(jsonPath("$[1].shifts", hasSize(0)));

        verify(teamService).getTeamMembersWithUpcomingShifts(employeeId);
    }

    @Test
    void getTeamEmployeesByManagerInCreateShiftForm_returns200AndList() throws Exception {
        String managerId = "ffffffff-ffff-ffff-ffff-ffffffffffff";
        TeamEmployeesShiftFormResponseDTO r1 = new TeamEmployeesShiftFormResponseDTO(
                UUID.fromString("88888888-8888-8888-8888-888888888888"),
                "A", "One"
        );
        TeamEmployeesShiftFormResponseDTO r2 = new TeamEmployeesShiftFormResponseDTO(
                UUID.fromString("99999999-9999-9999-9999-999999999999"),
                "B", "Two"
        );

        when(teamService.getTeamEmployeesByManagerInCreateShiftForm(managerId)).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/teams/manager/{managerId}/team-employees", managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("A"));

        verify(teamService).getTeamEmployeesByManagerInCreateShiftForm(eq(managerId));
    }
}
