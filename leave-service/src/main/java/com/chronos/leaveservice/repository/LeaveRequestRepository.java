package com.chronos.leaveservice.repository;

import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.leaveservice.dto.leaveRequests.EmployeeLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    Optional<LeaveRequest> findByLeaveRequestId(String leaveRequestId);

    List<LeaveRequest> findByEmployeeIdOrderByRequestDateDesc(UUID employeeId);

    List<LeaveRequest> findByEmployeeIdInOrderByRequestDateDesc(List<UUID> employeeIds);

    long countByEmployeeIdInAndLeaveStatus(List<UUID> employeeIds, LeaveStatus leaveStatus);

    @Query("""
           SELECT
                COUNT(lr)
           FROM
                LeaveRequest lr
           WHERE
                lr.employeeId in :employeeIds
                AND lr.leaveStatus = 'APPROVED'
                AND :today BETWEEN lr.startDate AND lr.endDate
           """)
    long countByOnLeaveToday(@Param("employeeIds") List<UUID> employeeIds, @Param("today") LocalDate today);


    @Query("""
           select new com.chronos.leaveservice.dto.leaveRequests.EmployeeLeaveRequestDashboardResponseDTO(
                lr.leaveRequestId,
                lr.leaveType,
                lr.startDate,
                lr.endDate,
                lr.leaveStatus
           )
           from
                LeaveRequest lr
           where
                lr.employeeId = :employeeId
           """)
    List<EmployeeLeaveRequestDashboardResponseDTO> leaveRequestEmployeeDashboard(@Param("employeeId") UUID employeeId);


    List<LeaveRequest> findLeaveRequestsByEmployeeId(UUID empID);
}
