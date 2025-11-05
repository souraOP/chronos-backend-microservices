package com.chronos.shiftservice;

import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.constants.enums.ShiftType;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.ResourceNotFoundException;
import com.chronos.common.exception.custom.ShiftNotFoundException;
import com.chronos.shiftservice.dto.shift.CreateShiftDateRequestDTO;
import com.chronos.shiftservice.dto.shift.ShiftResponseDTO;
import com.chronos.shiftservice.dto.shift.TeamShiftTableRowDTO;
import com.chronos.shiftservice.entity.Shift;
import com.chronos.shiftservice.feign.EmployeeClient;
import com.chronos.shiftservice.repository.ShiftRepository;
import com.chronos.shiftservice.service.impl.ShiftServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    @Test
    void createShift_success() {
        UUID empId = UUID.randomUUID();
        String managerId = "manager-1";

        // mock request DTO
        CreateShiftDateRequestDTO request = mock(CreateShiftDateRequestDTO.class);
        when(request.employeeId()).thenReturn(empId);
        when(request.shiftDate()).thenReturn(LocalDate.of(2025, 1, 10));
        when(request.shiftStartTime()).thenReturn(LocalTime.of(9, 0));
        when(request.shiftEndTime()).thenReturn(LocalTime.of(17, 0));
        when(request.shiftType()).thenReturn(ShiftType.REGULAR);
        when(request.shiftLocation()).thenReturn("HQ");

        // mock employee client returns team containing the employee
        EmployeeDTO empDto = mock(EmployeeDTO.class);
        when(empDto.id()).thenReturn(empId);
        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of(empDto));

        // mock save result
        Shift saved = new Shift();
        saved.setId(UUID.randomUUID());
        saved.setPublicId("SH-ABC123");
        saved.setEmployeeId(empId);
        saved.setShiftDate(LocalDate.of(2025, 1, 10));
        ZoneId zone = ZoneId.systemDefault();
        saved.setShiftStartTime(LocalDate.of(2025, 1, 10).atTime(9,0).atZone(zone).toOffsetDateTime());
        saved.setShiftEndTime(LocalDate.of(2025, 1, 10).atTime(17,0).atZone(zone).toOffsetDateTime());
        saved.setShiftType(ShiftType.REGULAR);
        saved.setShiftStatus(ShiftStatus.CONFIRMED);
        saved.setShiftLocation("HQ");

        when(shiftRepository.save(any(Shift.class))).thenReturn(saved);

        ShiftResponseDTO response = shiftService.createShift(request, managerId);

        assertNotNull(response);
        assertEquals("SH-ABC123", response.shiftId());
        assertEquals(LocalDate.of(2025, 1, 10), response.shiftDate());
        assertEquals(ShiftStatus.CONFIRMED, response.shiftStatus());
        assertEquals(ShiftType.REGULAR, response.shiftType());
    }

    @Test
    void createShift_employeeNotInTeam_throws() {
        UUID empId = UUID.randomUUID();
        String managerId = "manager-1";

        CreateShiftDateRequestDTO request = mock(CreateShiftDateRequestDTO.class);
        when(request.employeeId()).thenReturn(empId);

        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of()); // no team members

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> shiftService.createShift(request, managerId));
        assertNotNull(ex);
    }

    @Test
    void createShift_invalidTiming_throws() {
        UUID empId = UUID.randomUUID();
        String managerId = "manager-1";

        CreateShiftDateRequestDTO request = mock(CreateShiftDateRequestDTO.class);
        when(request.employeeId()).thenReturn(empId);
        when(request.shiftDate()).thenReturn(LocalDate.of(2025, 1, 10));
        // end time before start time
        when(request.shiftStartTime()).thenReturn(LocalTime.of(17, 0));
        when(request.shiftEndTime()).thenReturn(LocalTime.of(9, 0));
//        when(request.shiftType()).thenReturn(ShiftType.REGULAR);
//        when(request.shiftLocation()).thenReturn("HQ");

        EmployeeDTO empDto = mock(EmployeeDTO.class);
        when(empDto.id()).thenReturn(empId);
        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of(empDto));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> shiftService.createShift(request, managerId));
        assertEquals(ErrorConstants.INVALID_SHIFT_TIMING, ex.getMessage());
    }

    @Test
    void getEmployeeShifts_success() {
        UUID empId = UUID.randomUUID();
        String empIdStr = empId.toString();

        ZoneId zone = ZoneId.systemDefault();
        OffsetDateTime start = LocalDate.of(2025, 1, 10).atTime(9,0).atZone(zone).toOffsetDateTime();
        OffsetDateTime end = LocalDate.of(2025, 1, 10).atTime(17,0).atZone(zone).toOffsetDateTime();

        Shift s = new Shift();
        s.setId(UUID.randomUUID());
        s.setPublicId("SH-1");
        s.setEmployeeId(empId);
        s.setShiftDate(LocalDate.of(2025, 1, 10));
        s.setShiftStartTime(start);
        s.setShiftEndTime(end);
        s.setShiftStatus(ShiftStatus.CONFIRMED);
        s.setShiftType(ShiftType.REGULAR);
        s.setShiftLocation("HQ");

        when(shiftRepository.findShiftByEmployeeIdAndDateAsc(empId)).thenReturn(Optional.of(List.of(s)));

        List<ShiftResponseDTO> res = shiftService.getEmployeeShifts(empIdStr);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("SH-1", res.get(0).shiftId());
    }

    @Test
    void getEmployeeShifts_notFound_throws() {
        UUID empId = UUID.randomUUID();
        when(shiftRepository.findShiftByEmployeeIdAndDateAsc(empId)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> shiftService.getEmployeeShifts(empId.toString()));
    }

    @Test
    void getTeamsShiftByManager_success() {
        String managerId = "manager-1";
        UUID emp1 = UUID.randomUUID();
        UUID emp2 = UUID.randomUUID();

        EmployeeDTO e1 = mock(EmployeeDTO.class);
        when(e1.id()).thenReturn(emp1);
        EmployeeDTO e2 = mock(EmployeeDTO.class);
        when(e2.id()).thenReturn(emp2);
        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of(e1, e2));

        Shift s1 = new Shift();
        s1.setId(UUID.randomUUID());
        s1.setPublicId("SH-A");
        s1.setEmployeeId(emp1);
        s1.setShiftDate(LocalDate.of(2025, 1, 11));
        s1.setShiftStartTime(OffsetDateTime.now());
        s1.setShiftEndTime(OffsetDateTime.now().plusHours(8));
        s1.setShiftType(ShiftType.REGULAR);
        s1.setShiftStatus(ShiftStatus.CONFIRMED);
        s1.setShiftLocation("L1");

        Shift s2 = new Shift();
        s2.setId(UUID.randomUUID());
        s2.setPublicId("SH-B");
        s2.setEmployeeId(emp2);
        s2.setShiftDate(LocalDate.of(2025, 1, 12));
        s2.setShiftStartTime(OffsetDateTime.now());
        s2.setShiftEndTime(OffsetDateTime.now().plusHours(8));
        s2.setShiftType(ShiftType.NIGHT);
        s2.setShiftStatus(ShiftStatus.CONFIRMED);
        s2.setShiftLocation("L2");

        when(shiftRepository.findMultipleShiftByEmployeeId(List.of(emp1, emp2))).thenReturn(List.of(s1, s2));

        List<ShiftResponseDTO> res = shiftService.getTeamsShiftByManager(managerId);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(r -> r.shiftId().equals("SH-A")));
        assertTrue(res.stream().anyMatch(r -> r.shiftId().equals("SH-B")));
    }

    @Test
    void getTeamsShiftByManager_noTeam_returnsEmpty() {
        String managerId = "manager-1";
        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of());

        List<ShiftResponseDTO> res = shiftService.getTeamsShiftByManager(managerId);

        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    @Test
    void getTeamShiftsByManagerAndDatePicker_success() {
        String managerId = "manager-1";
        LocalDate date = LocalDate.of(2025, 1, 15);
        UUID emp1 = UUID.randomUUID();

        EmployeeDTO e1 = mock(EmployeeDTO.class);
        when(e1.id()).thenReturn(emp1);
        when(e1.firstName()).thenReturn("Alice");
        when(e1.lastName()).thenReturn(""); // tests fallback to first name only
        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of(e1));

        Shift s = new Shift();
        s.setId(UUID.randomUUID());
        s.setPublicId("SH-ROW");
        s.setEmployeeId(emp1);
        s.setShiftDate(date);
        s.setShiftStartTime(OffsetDateTime.now());
        s.setShiftEndTime(OffsetDateTime.now().plusHours(8));
        s.setShiftType(ShiftType.REGULAR);
        s.setShiftLocation("Office");
        s.setShiftStatus(ShiftStatus.CONFIRMED);

        when(shiftRepository.findTeamShiftRowByEmployeeIdsAndDateBetween(List.of(emp1), date)).thenReturn(List.of(s));

        List<TeamShiftTableRowDTO> rows = shiftService.getTeamShiftsByManagerAndDatePicker(managerId, date);

        assertNotNull(rows);
        assertEquals(1, rows.size());
        assertEquals("Alice", rows.get(0).employeeName());
        assertEquals("SH-ROW", rows.get(0).shiftId());
    }

    @Test
    void getTeamShiftsByManagerAndDatePicker_managerNoTeam_throws() {
        String managerId = "manager-1";
        LocalDate date = LocalDate.now();
        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> shiftService.getTeamShiftsByManagerAndDatePicker(managerId, date));
    }

    @Test
    void getDefaultTeamShiftByManager_returnsEmptyOnFallback() {
        List<ShiftResponseDTO> res = shiftService.getDefaultTeamShiftByManager("manager-1", new RuntimeException("boom"));
        assertNotNull(res);
        assertTrue(res.isEmpty());
    }
}
