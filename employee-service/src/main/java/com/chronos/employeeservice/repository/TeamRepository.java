package com.chronos.employeeservice.repository;

import com.chronos.employeeservice.dto.TeamEmployeesShiftFormResponseDTO;
import com.chronos.employeeservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    Optional<Team> findByTeamManagerId(UUID id);

    @Query("""
            select
                 e.id
            from Team t join t.employees e
                 where t.teamManager.id = :managerId
            """)
    List<UUID> findEmployeeIdsByManagerId(@Param("managerId") UUID managerId);


    @Query("select COUNT(e.id) from Employee e where e.team.teamManager.id = :managerId")
    long countTeamEmployeesByManagerId(@Param("managerId") UUID managerId);

// used in the create shift form
    @Query("""
           select new com.chronos.employeeservice.dto.TeamEmployeesShiftFormResponseDTO(
                e.id as id,
                e.firstName as firstName,
                e.lastName as lastName
           )
           from
                Team t join t.employees e
           where
                t.teamManager.id = :managerId
           """)
    List<TeamEmployeesShiftFormResponseDTO> findTeamEmployeesByManager(@Param("managerId") UUID managerId);
}
