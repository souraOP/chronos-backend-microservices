package com.chronos.reportservice.service.impl;

import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.enums.AttendanceStatus;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.reportservice.dto.AttendanceResponseDTO;
import com.chronos.reportservice.dto.ReportResponseDTO;
import com.chronos.reportservice.entity.Report;
import com.chronos.reportservice.feign.AttendanceServiceClient;
import com.chronos.reportservice.feign.EmployeeServiceClient;
import com.chronos.reportservice.repository.ReportRepository;
import com.chronos.reportservice.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chronos.reportservice.util.CountWorkingDays.countWorkingDays;
import static com.chronos.reportservice.util.ReportIdGenerator.generateReportId;
import static com.chronos.reportservice.util.RoundOffToTwo.round2;
import static com.chronos.reportservice.util.mapper.ReportMapper.toDto;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private final AttendanceServiceClient attendanceServiceClient;

    public ReportServiceImpl(
            ReportRepository reportRepository,
            EmployeeServiceClient employeeServiceClient,
            AttendanceServiceClient attendanceServiceClient
    ) {
        this.reportRepository = reportRepository;
        this.employeeServiceClient = employeeServiceClient;
        this.attendanceServiceClient = attendanceServiceClient;
    }

    @Override
    @Transactional
    public ReportResponseDTO generatedReportForManager(String managerId, LocalDate startDate, LocalDate endDate) {
        log.info("Invoked the generatedReportForManager service method, managerId:{}, startDate:{}, endDate:{}", managerId, startDate, endDate);

        // fetching the team members from the employee service
        List<EmployeeDTO> members = employeeServiceClient.getTeamMembers(managerId);
        if (members == null || members.isEmpty()) {
            throw new IllegalStateException(ErrorConstants.MANAGER_WITH_NO_TEAM);
        }

        String teamId = members.getFirst().teamId();


        // calculating the number of working days from the range given

        int workingDays = countWorkingDays(startDate, endDate);
        int teamSize = members.size();

        int totalPossibleDays = workingDays * teamSize;

        int totalDaysPresent = 0;
        double totalHoursWorked = 0.0;

        for (EmployeeDTO e : members) {
            UUID empUUID = e.id();
            if (empUUID == null) {
                continue;
            }

            List<AttendanceResponseDTO> history = attendanceServiceClient.getAttendanceHistory(empUUID.toString());
            if (history == null || history.isEmpty()) {
                continue;
            }

            List<AttendanceResponseDTO>  inRangeActive = history.stream()
                    .filter(a -> a.date() != null
                    &&  !a.date().isBefore(startDate) && !a.date().isAfter(endDate) && a.attendanceStatus() == AttendanceStatus.COMPLETE
                    ).toList();

            int empPresentDays = inRangeActive.stream()
                    .map(AttendanceResponseDTO::date)
                    .collect(Collectors.toSet())
                    .size();

            totalDaysPresent += empPresentDays;

            totalHoursWorked += inRangeActive.stream()
                    .mapToDouble(AttendanceResponseDTO::hoursWorked)
                    .sum();
        }

        int totalDaysAbsent = Math.max(0, totalPossibleDays - totalDaysPresent);


        Report report = new Report();
        report.setReportId(generateReportId());
        report.setTeamId(teamId);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalDaysPresent(totalDaysPresent);
        report.setTotalDaysAbsent(totalDaysAbsent);
        report.setTotalHoursWorked(round2(totalHoursWorked));

        Report savedReport = reportRepository.save(report);

        return toDto(savedReport);
    }

    @Override
    public List<ReportResponseDTO> getRecentReportsForManager(String managerId) {
        log.info("Invoked the getRecentReportsForManager service method, managerId:{}", managerId);

        List<EmployeeDTO> members = employeeServiceClient.getTeamMembers(managerId);
        if(members == null || members.isEmpty()) {
            return List.of();
        }

        String teamId = members.getFirst().teamId();
        if(teamId == null) {
            return List.of();
        }


        return getRecentReportsForTeam(teamId);
    }

    @Override
    public List<ReportResponseDTO> getRecentReportsForTeam(String teamId) {
        log.info("Invoked the getRecentReportsForTeam service method, teamId:{}", teamId);
        return reportRepository.findByTeamIdOrderByGeneratedAtDesc(teamId)
                .stream().map(s -> toDto(s)).toList();
    }
}
