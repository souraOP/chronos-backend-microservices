package com.chronos.employeeservice.controller;


import com.chronos.employeeservice.dto.TeamDTO;
import com.chronos.employeeservice.dto.TeamEmployeesShiftFormResponseDTO;
import com.chronos.employeeservice.dto.TeamMembersShiftDTO;
import com.chronos.employeeservice.dto.employee.EmployeeDTO;
import com.chronos.employeeservice.service.impl.TeamServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/teams")
@CrossOrigin("*")
public class TeamController {
    private final TeamServiceImpl teamService;

    @Autowired
    public TeamController(TeamServiceImpl teamService) {
        this.teamService = teamService;
    }

    // this endpoint for development purpose only
    // here im creating the team -> taking input as teamDTO

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        TeamDTO createTeam = teamService.createTeam(teamDTO);
        return new ResponseEntity<>(createTeam, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/teamSize")
    public ResponseEntity<Integer> getTeamSize(@PathVariable String managerId) {
        int teamSize = teamService.getTeamSize(managerId);
        return new ResponseEntity<>(teamSize, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable String teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok().build();
    }


//    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/team-members")
    public ResponseEntity<List<EmployeeDTO>> getTeamMembers(@PathVariable String managerId) {
        List<EmployeeDTO> getTeam = teamService.getTeamMembers(managerId);
        return new ResponseEntity<>(getTeam, HttpStatus.OK);
    }


    // for usage in the create shift swap form
    // only for employee usage
    @GetMapping("/{employeeId}/members-with-upcoming-shifts")
    public ResponseEntity<List<TeamMembersShiftDTO>> getTeamMembersWithUpcomingShifts(@PathVariable("employeeId") String employeeId) {
        List<TeamMembersShiftDTO> result = teamService.getTeamMembersWithUpcomingShifts(employeeId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/manager/{managerId}/team-employees")
    public ResponseEntity<List<TeamEmployeesShiftFormResponseDTO>> getTeamEmployeesByManagerInCreateShiftForm(@PathVariable("managerId") String managerId){
        List<TeamEmployeesShiftFormResponseDTO> getEmployees = teamService.getTeamEmployeesByManagerInCreateShiftForm(managerId);
        return new ResponseEntity<>(getEmployees, HttpStatus.OK);
    }
}
