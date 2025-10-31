package com.chronos.shiftservice.entity;

import com.chronos.common.constants.enums.ShiftSwapRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "shift_swap_requests")
@EntityListeners(AuditingEntityListener.class)
public class ShiftSwapRequest extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "shift_swap_id", length = 20, nullable = false, unique = true)
    private String publicId;

    @Column(name = "requester_employee_uuid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID requesterEmployeeId;

    @Column(name = "requested_employee_uuid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID requestedEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "offering_shift_id", referencedColumnName = "id", nullable = false)
    private Shift offeringShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "requesting_shift_id", referencedColumnName = "id", nullable = false)
    private Shift requestingShift;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_swap_request_status", nullable = false)
    @ColumnDefault("'PENDING'")
    private ShiftSwapRequestStatus status;

    @NotBlank
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "approved_by_id", nullable = true)
    private UUID approvedBy;

    @Column(name = "approved_date")
    private OffsetDateTime approvedDate;

    @PrePersist
    @PreUpdate
    private void validateEmployee() {
        if (requesterEmployeeId != null && requesterEmployeeId.equals(requestedEmployeeId)) {
            throw new IllegalArgumentException("Requester and Requested employee id must be different");
        }
    }
}
