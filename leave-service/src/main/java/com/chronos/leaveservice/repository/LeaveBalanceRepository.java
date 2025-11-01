package com.chronos.leaveservice.repository;

import com.chronos.common.constants.enums.LeaveType;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO;
import com.chronos.leaveservice.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {
    boolean existsByEmployeeIdAndLeaveType(UUID employeeId, LeaveType leaveType);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveType(@Param("employeeId") UUID employeeId, @Param("leaveType") LeaveType leaveType);

    @Query("""
            select new com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO(
                 lb.balanceId as balanceId,
                 lb.leaveType as leaveType,
                 lb.leaveBalance as leaveBalance
            )
            from LeaveBalance lb
            where
                 lb.employeeId = :employeeId
            """)
    Optional<List<LeaveBalanceResponseDTO>> findLeaveBalanceViewByEmployeeId(@Param("employeeId") UUID employeeId);
}
