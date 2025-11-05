package com.chronos.reportservice.controller;

import com.chronos.reportservice.dto.GeneratedReportRequestDTO;
import com.chronos.reportservice.dto.ReportResponseDTO;
import com.chronos.reportservice.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/manager/{managerId}/generate")
    public ResponseEntity<ReportResponseDTO> generate(@PathVariable("managerId") String managerId, @Valid @RequestBody GeneratedReportRequestDTO dto) {
        ReportResponseDTO result = reportService.generatedReportForManager(managerId, dto.startDate(), dto.endDate());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/manager/{managerId}/recent")
    public ResponseEntity<List<ReportResponseDTO>> recentByManager(@PathVariable("managerId") String managerId){
        List<ReportResponseDTO> results = reportService.getRecentReportsForManager(managerId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/team/{teamId}/recent")
    public ResponseEntity<List<ReportResponseDTO>> recentByTeam(@PathVariable("teamId") String teamId) {
        List<ReportResponseDTO> result = reportService.getRecentReportsForTeam(teamId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
