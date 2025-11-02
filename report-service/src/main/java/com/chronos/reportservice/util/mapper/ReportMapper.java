package com.chronos.reportservice.util.mapper;

import com.chronos.reportservice.dto.ReportResponseDTO;
import com.chronos.reportservice.entity.Report;

public class ReportMapper {
    public static ReportResponseDTO toDto(Report r) {
        return new ReportResponseDTO(
                r.getId(),
                r.getReportId(),
                r.getTeamId(),
                r.getStartDate(),
                r.getEndDate(),
                r.getTotalDaysPresent(),
                r.getTotalDaysAbsent(),
                r.getTotalHoursWorked(),
                r.getGeneratedAt()
        );
    }
}
