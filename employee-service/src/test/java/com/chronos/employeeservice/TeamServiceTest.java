// java
package com.chronos.employeeservice;

import com.chronos.common.constants.enums.Gender;
import com.chronos.common.constants.enums.Role;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.dto.ShiftCardDTO;
import com.chronos.employeeservice.dto.TeamEmployeesShiftFormResponseDTO;
import com.chronos.employeeservice.dto.TeamMembersShiftDTO;
import com.chronos.employeeservice.dto.UpcomingShiftsRequestDTO;
import com.chronos.employeeservice.dto.TeamDTO;
import com.chronos.employeeservice.entity.Employee;
import com.chronos.employeeservice.entity.Team;
import com.chronos.employeeservice.feign.ShiftClient;
import com.chronos.employeeservice.repository.EmployeeRepository;
import com.chronos.employeeservice.repository.TeamRepository;
import com.chronos.employeeservice.service.impl.TeamServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock TeamRepository teamRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock ShiftClient shiftClient;

    @InjectMocks TeamServiceImpl service;

    @Test
    void createTeam_setsManagerAndEmployees_andSaves() {
        UUID managerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID e1Id = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID e2Id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

        TeamDTO req = new TeamDTO("TEAM-1", "Alpha Team", managerId, List.of(e1Id, e2Id));

        Employee manager = emp(managerId, "Mgr", "One", null);
        Employee e1 = emp(e1Id, "Alice", "A", null);
        Employee e2 = emp(e2Id, "Bob", "B", null);

        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(employeeRepository.findById(e1Id)).thenReturn(Optional.of(e1));
        when(employeeRepository.findById(e2Id)).thenReturn(Optional.of(e2));
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        when(employeeRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        TeamDTO out = service.createTeam(req);

        assertEquals(req, out);

        ArgumentCaptor<Team> teamCap = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(teamCap.capture());
        Team saved = teamCap.getValue();

        assertEquals("TEAM-1", saved.getTeamId());
        assertEquals("Alpha Team", saved.getTeamName());
        assertSame(manager, saved.getTeamManager());
        assertEquals(2, saved.getEmployees().size());
        saved.getEmployees().forEach(emp -> assertSame(saved, emp.getTeam()));

        verify(employeeRepository).saveAll(saved.getEmployees());
    }

    @Test
    void getTeamMembers_returnsMappedDtos() {
        UUID managerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Team t = new Team();
        t.setTeamId("TEAM-1");
        t.setTeamName("Alpha");

        Employee e1 = emp(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Sourasish", "A", t);
        Employee e2 = emp(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Dinesh", "B", t);
        t.setEmployees(List.of(e1, e2));

        when(teamRepository.findByTeamManagerId(managerId)).thenReturn(Optional.of(t));

        List<EmployeeDTO> out = service.getTeamMembers(managerId.toString());

        assertEquals(2, out.size());
        assertEquals("Sourasish", out.get(0).firstName());
        assertEquals("TEAM-1", out.get(0).teamId());
        verify(teamRepository).findByTeamManagerId(managerId);
    }

    @Test
    void getTeamSize_returnsCount() {
        UUID managerId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(teamRepository.countTeamEmployeesByManagerId(managerId)).thenReturn(3L);

        int size = service.getTeamSize(managerId.toString());

        assertEquals(3, size);
        verify(teamRepository).countTeamEmployeesByManagerId(managerId);
    }

    @Test
    void deleteTeam_whenExists_deletes() {
        UUID teamId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        when(teamRepository.existsById(teamId)).thenReturn(true);

        service.deleteTeam(teamId.toString());

        verify(teamRepository).deleteById(teamId);
    }

    @Test
    void getTeamMembersWithUpcomingShifts_mergesAndSorts() {
        UUID selfId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID id1 = UUID.fromString("77777777-7777-7777-7777-777777777777"); // Bob
        UUID id2 = UUID.fromString("88888888-8888-8888-8888-888888888888"); // Alice

        Employee sourasish = emp(id1, "Sourasish", "B", null);
        Employee dinesh = emp(id2, "Dinesh", "A", null);

        when(employeeRepository.findTeamEmployeesExcludingSelfAndManager(selfId))
                .thenReturn(List.of(sourasish, dinesh));

        Map<String, List<ShiftCardDTO>> shifts = new HashMap<>();
        shifts.put(id1.toString(), List.of(mock(ShiftCardDTO.class)));
        when(shiftClient.getUpcomingByEmployeeIds(any(UpcomingShiftsRequestDTO.class))).thenReturn(shifts);

        List<TeamMembersShiftDTO> out = service.getTeamMembersWithUpcomingShifts(selfId.toString());

        assertEquals(2, out.size());

        assertEquals("Dinesh", out.get(0).firstName());
        assertEquals("Sourasish", out.get(1).firstName());

        assertEquals(0, out.get(0).shifts().size());
        assertEquals(1, out.get(1).shifts().size());

        verify(employeeRepository).findTeamEmployeesExcludingSelfAndManager(selfId);
        verify(shiftClient).getUpcomingByEmployeeIds(any(UpcomingShiftsRequestDTO.class));
    }

    @Test
    void getTeamEmployeesByManagerInCreateShiftForm_returnsList() {
        UUID managerId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        TeamEmployeesShiftFormResponseDTO r1 = new TeamEmployeesShiftFormResponseDTO(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "X", "One"
        );
        TeamEmployeesShiftFormResponseDTO r2 = new TeamEmployeesShiftFormResponseDTO(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "B", "Two"
        );

        when(teamRepository.findTeamEmployeesByManager(managerId)).thenReturn(List.of(r1, r2));

        List<TeamEmployeesShiftFormResponseDTO> out =
                service.getTeamEmployeesByManagerInCreateShiftForm(managerId.toString());

        assertEquals(2, out.size());
        assertEquals("X", out.get(0).firstName());
        verify(teamRepository).findTeamEmployeesByManager(managerId);
    }


    private static Employee emp(UUID id, String first, String last, Team team) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName(first);
        e.setLastName(last);
        e.setEmail(first.toLowerCase() + "@example.com");
        e.setGender(Gender.MALE);
        e.setPhoneNumber("000");
        e.setJobTitle("Dev");
        e.setActive(true);
        e.setDepartmentName("Eng");
        e.setRole(Role.EMPLOYEE);
        e.setTeam(team);
        return e;
    }
}
