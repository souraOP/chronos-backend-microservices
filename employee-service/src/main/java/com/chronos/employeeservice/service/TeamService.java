package com.chronos.employeeservice.service;


import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.dto.TeamDTO;
import com.chronos.employeeservice.dto.TeamEmployeesShiftFormResponseDTO;
import com.chronos.employeeservice.dto.TeamMembersShiftDTO;

import java.util.List;


public interface TeamService {
    TeamDTO createTeam(TeamDTO teamDTO);

    List<EmployeeDTO> getTeamMembers(String managerId);

    int getTeamSize(String managerId);

    void deleteTeam(String teamId);

    List<TeamMembersShiftDTO> getTeamMembersWithUpcomingShifts(String employeeId);

    List<TeamEmployeesShiftFormResponseDTO> getTeamEmployeesByManagerInCreateShiftForm(String managerId);
}
