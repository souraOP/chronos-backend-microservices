package com.chronos.employeeservice.service.impl;


import com.chronos.employeeservice.constants.ErrorConstants;
import com.chronos.employeeservice.constants.UuidErrorConstants;
import com.chronos.employeeservice.dto.*;
import com.chronos.employeeservice.dto.employee.EmployeeDTO;
import com.chronos.employeeservice.entity.Employee;
import com.chronos.employeeservice.entity.Team;
import com.chronos.employeeservice.feign.ShiftClient;
import com.chronos.employeeservice.repository.EmployeeRepository;
import com.chronos.employeeservice.repository.TeamRepository;
import com.chronos.employeeservice.service.TeamService;
import com.chronos.employeeservice.util.mappers.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.chronos.employeeservice.util.ParseUUID.parseUUID;


@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftClient shiftClient;

    @Autowired
    public TeamServiceImpl(
            TeamRepository teamRepository,
            EmployeeRepository employeeRepository,
            ShiftClient shiftClient
    ) {
        this.teamRepository = teamRepository;
        this.employeeRepository = employeeRepository;
        this.shiftClient = shiftClient;
    }

    @Override
    @Transactional
    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = new Team();
        team.setTeamId(teamDTO.teamId());
        team.setTeamName(teamDTO.teamName());

        Employee manager = employeeRepository.findById(teamDTO.teamManagersId())
                .orElseThrow(() -> new RuntimeException(ErrorConstants.MANAGER_NOT_FOUND));

        team.setTeamManager(manager);
//        Team savedTeam = teamRepository.save(team);
//        manager.setTeam(savedTeam);

        List<Employee> employees = new ArrayList<>();
        for (UUID employeeId : teamDTO.employeeIds()) {
            Employee empl = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException(ErrorConstants.EMPLOYEE_NOT_FOUND + employeeId));

            empl.setTeam(team);
            employees.add(empl);
        }
        team.setEmployees(employees);

//        employeeRepository.save(manager);

        teamRepository.save(team);
        employeeRepository.saveAll(employees);
        return teamDTO;
    }

    @Override
    public List<EmployeeDTO> getTeamMembers(String managerId) {
        UUID mngID = parseUUID(managerId, UuidErrorConstants.INVALID_MANAGER_UUID);

        Team team = teamRepository.findByTeamManagerId(mngID)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.MANAGER_WITH_NO_TEAM + managerId));

        return team.getEmployees()
                .stream()
                .map(EmployeeMapper::employeeEntityToDto)
                .toList();
    }

    @Override
    public int getTeamSize(String managerId){
        UUID mngID = parseUUID(managerId, UuidErrorConstants.INVALID_MANAGER_UUID);

        long getTeamCount = teamRepository.countTeamEmployeesByManagerId(mngID);

        return Math.toIntExact(getTeamCount);
    }

    @Override
    @Transactional
    public void deleteTeam(String teamId){
        UUID teamID = parseUUID(teamId, UuidErrorConstants.INVALID_TEAM_ID);

        if(!teamRepository.existsById(teamID)) {
            throw new RuntimeException(ErrorConstants.EMPLOYEE_WITH_NO_TEAM);
        }

        teamRepository.deleteById(teamID);
    }

    @Override
    public List<TeamMembersShiftDTO> getTeamMembersWithUpcomingShifts(String employeeId){
        UUID empID = parseUUID(employeeId, UuidErrorConstants.INVALID_EMPLOYEE_UUID);

        List<Employee> teamEmployees = employeeRepository.findTeamEmployeesExcludingSelfAndManager(empID);

        if(teamEmployees.isEmpty()) {
            return List.of();
        }

        List<UUID> teamEmpIds = teamEmployees.stream().map(p -> p.getId()).toList();

        Map<String, List<ShiftCardDTO>> upcomingByEmp = shiftClient.getUpcomingByEmployeeIds(new UpcomingShiftsRequestDTO(teamEmpIds));

        Map<UUID, List<ShiftCardDTO>> shiftByEmp = upcomingByEmp.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> UUID.fromString(e.getKey()),
                        Map.Entry::getValue
                ));

        return teamEmployees.stream().map(p -> new TeamMembersShiftDTO(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                shiftByEmp.getOrDefault(p.getId(), List.of())
        )).sorted(Comparator.comparing(TeamMembersShiftDTO::firstName)
                .thenComparing(TeamMembersShiftDTO::lastName)).toList();
    }

    @Override
    public List<TeamEmployeesShiftFormResponseDTO> getTeamEmployeesByManagerInCreateShiftForm(String managerId) {
        UUID managerID = parseUUID(managerId, UuidErrorConstants.INVALID_MANAGER_UUID);

        return teamRepository.findTeamEmployeesByManager(managerID);
    }
}
