package com.chronos.shiftservice.repository;


import com.chronos.shiftservice.entity.Shift;
import com.chronos.shiftservice.repository.projections.EmployeeShiftView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    @Query("""
            SELECT s
            FROM Shift s
            WHERE s.employeeId = :empID
            ORDER BY s.shiftDate asc, s.shiftStartTime asc
            """)
    Optional<List<Shift>> findShiftByEmployeeIdAndDateAsc(@Param("empID") UUID employeeId);


    @Query("""
            SELECT
                s
            FROM Shift s
                 WHERE s.employeeId in :employeeIds
            ORDER BY s.shiftDate asc, s.shiftStartTime asc
            """)
    List<Shift> findMultipleShiftByEmployeeId(@Param("employeeIds") List<UUID> employeeIds);


    @Query("""
            select s
            from
                 Shift s
            where
                 s.employeeId in :employeeIds and s.shiftDate = :date
            order by s.shiftDate asc, s.shiftStartTime asc
            """)
    List<Shift> findTeamShiftRowByEmployeeIdsAndDateBetween(@Param("employeeIds") List<UUID> employeeIds, @Param("date") LocalDate date);


    @Query("""
           select
                s.id as id,
                s.publicId as shiftId,
                s.employeeId as employeeId,
                s.shiftDate as shiftDate,
                s.shiftStartTime as shiftStartTime,
                s.shiftEndTime as shiftEndTime,
                s.shiftStatus as shiftStatus,
                s.shiftType as shiftType,
                s.shiftLocation as shiftLocation
           from
                Shift s
           where
                s.employeeId in :empIds and s.shiftStartTime >= :now
           order by s.shiftDate, s.shiftStartTime
           """)
    List<EmployeeShiftView> findUpcomingShiftViewByEmployeeIds(@Param("empIds") List<UUID> employeeIds, @Param("now") OffsetDateTime now);
}
