package com.chronos.reportservice.service;

import com.chronos.reportservice.dto.ReportResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    ReportResponseDTO generatedReportForManager(String managerId, LocalDate startDate, LocalDate endDate);

    List<ReportResponseDTO> getRecentReportsForManager(String managerId, int size);

    List<ReportResponseDTO> getRecentReportsForTeam(String teamId, int size);
}
