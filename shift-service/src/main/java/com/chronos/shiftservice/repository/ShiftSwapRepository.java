package com.chronos.shiftservice.repository;

import com.chronos.shiftservice.entity.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftSwapRepository extends JpaRepository<ShiftSwapRequest, UUID> {

    @Query("""
           select ssr
           from ShiftSwapRequest ssr
           where ssr.requesterEmployeeId = :employeeId or ssr.requestedEmployeeId = :employeeId
           order by ssr.createdAt desc
           """)
    List<ShiftSwapRequest> findSwapRequestsByEmployee(UUID employeeId);


    @Query("""
           select ssr
           from ShiftSwapRequest ssr
           where ssr.requesterEmployeeId in :employeeIds or ssr.requestedEmployeeId in :employeeIds
           order by ssr.createdAt desc
           """)
    List<ShiftSwapRequest> findTeamSwapRequests(List<UUID> employeeIds);
}
