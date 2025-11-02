package com.chronos.employeeservice.repository;

import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO;
import com.chronos.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByDisplayEmployeeId(String displayEmployeeId);

    @Query("""
            select new com.chronos.common.dto.EmployeeDTO(
                 e.id,
                 e.displayEmployeeId,
                 e.firstName,
                 e.lastName,
                 e.email,
                 e.gender,
                 e.phoneNumber,
                 e.jobTitle,
                 e.isActive,
                 e.departmentName,
                 e.role,
                 coalesce(t.teamId, '[Not in a team]')
            )
            from
                 Employee e left join e.team t
            where e.id = :employeeId
            """)
    Optional<EmployeeDTO> findEmployeeByID(@Param("employeeId") UUID employeeId);

    @Query("""
           select new com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO(
                e.firstName,
                e.lastName
           )
           from
                Employee e
           where e.id = :empID
           """)
    EmployeeNameResponseDTO findEmployeeName(UUID empID);


    @Query("""
           select teamEmp
           from
                Employee e
                JOIN e.team t
                JOIN t.employees teamEmp
           where
                e.id = :employeeId
                and teamEmp.id <> :employeeId
                and teamEmp.isActive = true
                and (t.teamManager is null or teamEmp.id <> t.teamManager.id)
           """)
    List<Employee> findTeamEmployeesExcludingSelfAndManager(@Param("employeeId") UUID employeeId);
}
