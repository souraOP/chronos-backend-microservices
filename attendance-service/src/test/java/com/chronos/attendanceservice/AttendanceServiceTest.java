package com.chronos.attendanceservice;

import com.chronos.attendanceservice.dto.AttendanceResponseDTO;
import com.chronos.attendanceservice.dto.ManagerAttendanceDisplayByDateResponseDTO;
import com.chronos.attendanceservice.entity.Attendance;
import com.chronos.attendanceservice.feign.EmployeeClient;
import com.chronos.attendanceservice.repository.AttendanceRepository;
import com.chronos.attendanceservice.service.impl.AttendanceServiceImpl;
import com.chronos.common.constants.enums.AttendanceStatus;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.ActiveAttendanceExistsException;
import com.chronos.common.exception.custom.ActiveAttendanceNotFoundException;
import com.chronos.common.exception.custom.EmployeeNotFoundException;
import com.chronos.common.exception.custom.InvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private String employeeId;
    private UUID employeeUuid;

    @BeforeEach
    void setup() {
        employeeUuid = UUID.randomUUID();
        employeeId = employeeUuid.toString();
    }

    @Test
    void getLatestAttendance_whenListNotEmpty_returnsFirst() {
        AttendanceResponseDTO dto1 = new AttendanceResponseDTO("ATT-1", LocalDate.now(), null, null, 0.0, AttendanceStatus.ACTIVE, "Loc");
        AttendanceResponseDTO dto2 = new AttendanceResponseDTO("ATT-2", LocalDate.now().minusDays(1), null, null, 0.0, AttendanceStatus.COMPLETE, "Loc2");

        when(attendanceRepository.findAllByEmployeeOrderByDateDesc(employeeUuid)).thenReturn(List.of(dto1, dto2));

        AttendanceResponseDTO result = attendanceService.getLatestAttendance(employeeId);

        assertThat(result.attendanceId()).isEqualTo("ATT-1");
        verify(attendanceRepository, times(1)).findAllByEmployeeOrderByDateDesc(employeeUuid);
    }

    @Test
    void getLatestAttendance_whenEmpty_returnsDefault() {
        when(attendanceRepository.findAllByEmployeeOrderByDateDesc(employeeUuid)).thenReturn(List.of());

        AttendanceResponseDTO result = attendanceService.getLatestAttendance(employeeId);

        assertThat(result.attendanceId()).isEqualTo("N/A");
        assertThat(result.attendanceStatus()).isEqualTo(AttendanceStatus.COMPLETE);
    }

    @Test
    void getAttendanceHistory_returnsListFromRepo() {
        AttendanceResponseDTO dto = new AttendanceResponseDTO("ATT-H", LocalDate.now(), null, null, 0.0, AttendanceStatus.COMPLETE, null);
        when(attendanceRepository.findAllByEmployeeOrderByDateDesc(employeeUuid)).thenReturn(List.of(dto));

        List<AttendanceResponseDTO> history = attendanceService.getAttendanceHistory(employeeId);

        assertThat(history).hasSize(1).contains(dto);
    }

    @Test
    void checkIn_success_savesAndReturnsDto() {
        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        when(attendanceRepository.findLatestByEmployeeAndStatus(employeeUuid, AttendanceStatus.ACTIVE)).thenReturn(List.of());

        Attendance saved = new Attendance();
        saved.setAttendanceId("ATT-XYZ");
        saved.setEmployeeId(employeeUuid);
        saved.setDate(LocalDate.now());
        OffsetDateTime now = OffsetDateTime.now();
        saved.setCheckIn(now);
        saved.setCheckOut(null);
        saved.setHoursWorked(0.0);
        saved.setAttendanceStatus(AttendanceStatus.ACTIVE);
        saved.setLocation("Office");

        when(attendanceRepository.save(any(Attendance.class))).thenReturn(saved);

        AttendanceResponseDTO response = attendanceService.checkIn(employeeId, null);

        assertThat(response.attendanceId()).isEqualTo("ATT-XYZ");
        assertThat(response.attendanceStatus()).isEqualTo(AttendanceStatus.ACTIVE);
        assertThat(response.checkIn()).isEqualTo(now);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void checkIn_employeeNotFound_throws() {
        when(employeeClient.getEmployeeById(employeeId)).thenReturn(null);
        assertThatThrownBy(() -> attendanceService.checkIn(employeeId, null))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void checkIn_activeExists_throws() {
        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        Attendance active = new Attendance();
        active.setAttendanceStatus(AttendanceStatus.ACTIVE);
        when(attendanceRepository.findLatestByEmployeeAndStatus(employeeUuid, AttendanceStatus.ACTIVE))
                .thenReturn(List.of(active));

        assertThatThrownBy(() -> attendanceService.checkIn(employeeId, null))
                .isInstanceOf(ActiveAttendanceExistsException.class);
    }

    @Test
    void checkOut_success_updatesAndReturnsDto() {
        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));

        Attendance active = new Attendance();
        active.setAttendanceId("ATT-CO");
        active.setEmployeeId(employeeUuid);
        OffsetDateTime checkIn = OffsetDateTime.now().minusHours(2);
        active.setCheckIn(checkIn);
        active.setAttendanceStatus(AttendanceStatus.ACTIVE);
        active.setHoursWorked(0.0);

        when(attendanceRepository.findLatestByEmployeeAndStatus(employeeUuid, AttendanceStatus.ACTIVE))
                .thenReturn(List.of(active));

        // simulate save returning the updated attendance
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(inv -> inv.getArgument(0));

        AttendanceResponseDTO resp = attendanceService.checkOut(employeeId);

        assertThat(resp.attendanceStatus()).isEqualTo(AttendanceStatus.COMPLETE);
        assertThat(resp.hoursWorked()).isGreaterThan(0.0);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void checkOut_noActive_throws() {
        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        when(attendanceRepository.findLatestByEmployeeAndStatus(employeeUuid, AttendanceStatus.ACTIVE))
                .thenReturn(List.of());

        assertThatThrownBy(() -> attendanceService.checkOut(employeeId))
                .isInstanceOf(ActiveAttendanceNotFoundException.class);
    }

    @Test
    void getTeamsAttendanceByDate_managerNotFound_throws() {
        String managerId = UUID.randomUUID().toString();
        when(employeeClient.getEmployeeById(managerId)).thenReturn(null);

        assertThatThrownBy(() -> attendanceService.getTeamsAttendanceByDate(managerId, "2025-01-01"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void getTeamsAttendanceByDate_invalidDate_throws() {
        String managerId = UUID.randomUUID().toString();
        when(employeeClient.getEmployeeById(managerId)).thenReturn(mock(EmployeeDTO.class));

        assertThatThrownBy(() -> attendanceService.getTeamsAttendanceByDate(managerId, "invalid-date"))
                .isInstanceOf(InvalidDateException.class);
    }

    @Test
    void getTeamsAttendanceByDate_emptyTeam_returnsEmptyRows() {
        String managerId = UUID.randomUUID().toString();
        when(employeeClient.getEmployeeById(managerId)).thenReturn(mock(EmployeeDTO.class));
        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of());

        ManagerAttendanceDisplayByDateResponseDTO result = attendanceService.getTeamsAttendanceByDate(managerId, LocalDate.now().toString());

        assertThat(result.attendanceRows()).isEmpty();
    }

    @Test
    void getTeamsAttendanceByDate_success_mapsRows() {
        String managerId = UUID.randomUUID().toString();
        when(employeeClient.getEmployeeById(managerId)).thenReturn(mock(EmployeeDTO.class));

        // prepare team members (mocked EmployeeDTOs)
        EmployeeDTO e1 = mock(EmployeeDTO.class);
        UUID id1 = UUID.randomUUID();
        when(e1.id()).thenReturn(id1);
        when(e1.displayEmployeeId()).thenReturn("E-1");
        when(e1.firstName()).thenReturn("John");
        when(e1.lastName()).thenReturn("Doe");

        when(employeeClient.getTeamMembers(managerId)).thenReturn(List.of(e1));

        // prepare attendance rows
        Attendance a = new Attendance();
        a.setEmployeeId(id1);
        a.setCheckIn(OffsetDateTime.now().minusHours(1));
        a.setCheckOut(OffsetDateTime.now());
        a.setHoursWorked(1.0);
        a.setAttendanceStatus(AttendanceStatus.COMPLETE);

        when(attendanceRepository.findTeamAttendanceByDate(anyList(), any(LocalDate.class))).thenReturn(List.of(a));

        ManagerAttendanceDisplayByDateResponseDTO res = attendanceService.getTeamsAttendanceByDate(managerId, LocalDate.now().toString());

        assertThat(res.attendanceRows()).hasSize(1);
        assertThat(res.attendanceRows().get(0).displayEmployeeId()).isEqualTo("E-1");
        assertThat(res.attendanceRows().get(0).employeeName()).contains("John");
    }
}

