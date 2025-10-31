package com.chronos.shiftservice.entity;

import com.chronos.shiftservice.constants.enums.ShiftStatus;
import com.chronos.shiftservice.constants.enums.ShiftType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "shifts")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Shift extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "shift_id", length = 20, nullable = false, unique = true)
    private String publicId;

    @Column(name = "employee_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID employeeId;

    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @Column(name = "shift_start_time", nullable = false)
    private OffsetDateTime shiftStartTime;

    @Column(name = "shift_end_time", nullable = false)
    private OffsetDateTime shiftEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false)
    private ShiftType shiftType;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_status")
    @ColumnDefault("'CONFIRMED'")
    private ShiftStatus shiftStatus;

    @Column(name = "shift_location", length = 100)
    private String shiftLocation;

    // lists
    @OneToMany(mappedBy = "offeringShift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftSwapRequest> offeringShift;

    @OneToMany(mappedBy = "requestingShift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftSwapRequest> requestingShift;
}
