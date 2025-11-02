package com.chronos.reportservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "reports")
@EntityListeners(AuditingEntityListener.class)
public class Report extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "report_id", length = 20, nullable = false, unique = true)
    private String reportId;

    @Column(name = "team_id", length = 50, nullable = false)
    private String teamId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days_present")
    private Integer totalDaysPresent;

    @Column(name = "total_days_absent")
    private Integer totalDaysAbsent;

    @Column(name = "total_hours_worked")
    private Double totalHoursWorked;

    @CreatedDate
    @Column(name = "generated_at", updatable = false)
    private Instant generatedAt;
}
