package com.chronos.employeeservice.controller;


import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.ErrorResponse;
import com.chronos.employeeservice.dto.TeamDTO;
import com.chronos.employeeservice.dto.TeamEmployeesShiftFormResponseDTO;
import com.chronos.employeeservice.dto.TeamMembersShiftDTO;
import com.chronos.employeeservice.service.impl.TeamServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Team CRUD Rest API",
        description = "REST APIs - Create Team, Get Team Size, Delete Team, Get Team Members, Get Team Members With Shifts"
)
@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamServiceImpl teamService;

    @Autowired
    public TeamController(TeamServiceImpl teamService) {
        this.teamService = teamService;
    }

    // this endpoint for development purpose only
    // here im creating the team -> taking input as teamDTO


    @Operation(
            summary = "Create Team REST API",
            description = "Create a new team (development purpose only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created team",
                    content = @Content(schema = @Schema(implementation = TeamDTO.class)))
    })
    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        TeamDTO createTeam = teamService.createTeam(teamDTO);
        return new ResponseEntity<>(createTeam, HttpStatus.CREATED);
    }



    @Operation(
            summary = "Get Team Size REST API",
            description = "Retrieve the number of team members for a specific manager"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team size",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/teamSize")
    public ResponseEntity<Integer> getTeamSize(@PathVariable String managerId) {
        int teamSize = teamService.getTeamSize(managerId);
        return new ResponseEntity<>(teamSize, HttpStatus.OK);
    }




    @Operation(
            summary = "Delete Team REST API",
            description = "Permanently remove a team from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted team"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid team ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable String teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok().build();
    }




    // for getting the team members of that particular manager
    @Operation(
            summary = "Get Team Members REST API",
            description = "Retrieve all team members for a specific manager"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team members",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
//    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/team-members")
    public ResponseEntity<List<EmployeeDTO>> getTeamMembers(@PathVariable String managerId) {
        List<EmployeeDTO> getTeam = teamService.getTeamMembers(managerId);
        return new ResponseEntity<>(getTeam, HttpStatus.OK);
    }





    // for usage in the create shift swap form
    // only for employee usage
    @Operation(
            summary = "Get Team Members With Upcoming Shifts REST API",
            description = "Retrieve team members with their upcoming shift information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team members with shifts",
                    content = @Content(schema = @Schema(implementation = TeamMembersShiftDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid employee ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{employeeId}/members-with-upcoming-shifts")
    public ResponseEntity<List<TeamMembersShiftDTO>> getTeamMembersWithUpcomingShifts(@PathVariable("employeeId") String employeeId) {
        List<TeamMembersShiftDTO> result = teamService.getTeamMembersWithUpcomingShifts(employeeId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }




    @Operation(
            summary = "Get Team Employees For Shift Form REST API",
            description = "Retrieve team employees data formatted for shift creation form"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved team employees for shift form",
                    content = @Content(schema = @Schema(implementation = TeamEmployeesShiftFormResponseDTO[].class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/team-employees")
    public ResponseEntity<List<TeamEmployeesShiftFormResponseDTO>> getTeamEmployeesByManagerInCreateShiftForm(@PathVariable("managerId") String managerId){
        List<TeamEmployeesShiftFormResponseDTO> getEmployees = teamService.getTeamEmployeesByManagerInCreateShiftForm(managerId);
        return new ResponseEntity<>(getEmployees, HttpStatus.OK);
    }
}
