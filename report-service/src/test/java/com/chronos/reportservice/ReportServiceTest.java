package com.chronos.reportservice;

import com.chronos.reportservice.dto.ReportResponseDTO;
import com.chronos.reportservice.entity.Report;
import com.chronos.reportservice.feign.AttendanceServiceClient;
import com.chronos.reportservice.feign.EmployeeServiceClient;
import com.chronos.reportservice.repository.ReportRepository;
import com.chronos.reportservice.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private EmployeeServiceClient employeeServiceClient;

    @Mock
    private AttendanceServiceClient attendanceServiceClient;

    @InjectMocks
    private ReportServiceImpl service;


    @Test
    void generatedReportForManager_NoTeam_Throws() {
        String managerId = "m-1";
        when(employeeServiceClient.getTeamMembers(managerId)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () ->
                service.generatedReportForManager(managerId, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2))
        );

        verify(employeeServiceClient).getTeamMembers(managerId);
        verifyNoInteractions(attendanceServiceClient);
        verifyNoInteractions(reportRepository);
    }

    @Test
    void getRecentReportsForManager_NoTeam_EmptyList() {
        String managerId = "m-2";
        when(employeeServiceClient.getTeamMembers(managerId)).thenReturn(List.of());

        List<ReportResponseDTO> results = service.getRecentReportsForManager(managerId, 10);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(employeeServiceClient).getTeamMembers(managerId);
        verifyNoInteractions(reportRepository);
    }

    @Test
    void getRecentReportsForTeam_MapsEntitiesToDtos() {
        String teamId = "T1";
        LocalDate start = LocalDate.of(2025, 2, 3);
        LocalDate end = LocalDate.of(2025, 2, 7);
        Instant genAt = Instant.parse("2025-03-01T10:00:00Z");

        Report r = new Report();
        r.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        r.setReportId("RPT-123");
        r.setTeamId(teamId);
        r.setStartDate(start);
        r.setEndDate(end);
        r.setTotalDaysPresent(7);
        r.setTotalDaysAbsent(3);
        r.setTotalHoursWorked(40.5);
        r.setGeneratedAt(genAt);

        when(reportRepository.findByTeamIdOrderByGeneratedAtDesc(eq(teamId), any(Pageable.class)))
                .thenReturn(List.of(r));

        List<ReportResponseDTO> dtos = service.getRecentReportsForTeam(teamId, 5);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());

        ReportResponseDTO dto = dtos.get(0);
        assertEquals(r.getId(), dto.id());
        assertEquals("RPT-123", dto.reportId());
        assertEquals(teamId, dto.teamId());
        assertEquals(start, dto.startDate());
        assertEquals(end, dto.endDate());
        assertEquals(7, dto.totalDaysPresent());
        assertEquals(3, dto.totalDaysAbsent());
        assertEquals(40.5, dto.totalHoursWorked());
        assertEquals(genAt, dto.generatedAt());

        verify(reportRepository).findByTeamIdOrderByGeneratedAtDesc(eq(teamId), any(Pageable.class));
    }

    @Test
    void getRecentReportsForTeam_SizeZero_UsesAtLeastOne() {
        when(reportRepository.findByTeamIdOrderByGeneratedAtDesc(anyString(), any(Pageable.class)))
                .thenReturn(List.of());

        service.getRecentReportsForTeam("T1", 0);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(reportRepository).findByTeamIdOrderByGeneratedAtDesc(eq("T1"), captor.capture());
        assertEquals(1, captor.getValue().getPageSize());
    }
}
