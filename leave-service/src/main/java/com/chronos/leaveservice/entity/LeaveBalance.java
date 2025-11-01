package com.chronos.leaveservice.entity;

import com.chronos.common.constants.enums.LeaveType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leave_balances")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class LeaveBalance extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "balance_id", length = 20, nullable = false, unique = true)
    private String balanceId;

    @Column(name = "employee_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;

    @Column(name = "leave_balance", nullable = false)
    private int leaveBalance;
}
