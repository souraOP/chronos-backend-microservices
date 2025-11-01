package com.chronos.attendanceservice.entity;

import com.chronos.common.constants.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendances")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Attendance extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "attendance_id", length = 20, nullable = false)
    private String attendanceId;

    @Column(name = "employee_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID employeeId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "check_in")
    private OffsetDateTime checkIn;

    @Column(name = "check_out")
    private OffsetDateTime checkOut;

    @Column(name = "hours_worked")
    private double hoursWorked;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false)
    @ColumnDefault("'ACTIVE'")
    private AttendanceStatus attendanceStatus;

    @Column(name = "location", length = 100)
    private String location;
}
