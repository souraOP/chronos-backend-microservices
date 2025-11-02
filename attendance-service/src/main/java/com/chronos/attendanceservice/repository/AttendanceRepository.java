package com.chronos.attendanceservice.repository;

import com.chronos.attendanceservice.dto.AttendanceResponseDTO;
import com.chronos.attendanceservice.entity.Attendance;
import com.chronos.common.constants.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    @Query("""
            select new com.chronos.attendanceservice.dto.AttendanceResponseDTO(
                 a.attendanceId,
                 a.date,
                 a.checkIn,
                 a.checkOut,
                 a.hoursWorked,
                 a.attendanceStatus,
                 a.location
            )
            from Attendance a
            where a.employeeId = :employeeId
            order by a.date desc
            """)
    List<AttendanceResponseDTO> findAllByEmployeeOrderByDateDesc(@Param("employeeId") UUID employeeId);

    @Query("""
            select
                 a from Attendance a
            where
                 a.employeeId = :employeeId and a.attendanceStatus = :status
            ORDER by a.date desc
            """)
    List<Attendance> findLatestByEmployeeAndStatus(@Param("employeeId") UUID employeeId, @Param("status") AttendanceStatus status);

    @Query("""
            select a
            from Attendance a
            where a.employeeId in :employeeIds
            and a.date = :date
            order by a.employeeId asc, a.checkIn asc
            """)
    List<Attendance> findTeamAttendanceByDate(@Param("employeeIds") List<UUID> employeeIds, @Param("date") LocalDate date);
}

