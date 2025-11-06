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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that manages team operations and team member information.
 * <p>
 * Responsibilities:
 * - Create new teams (development purpose).
 * - Retrieve team size for a specific manager.
 * - Delete teams from the system.
 * - Retrieve all team members for a manager.
 * - Retrieve team members with upcoming shift information.
 * - Retrieve team employees formatted for shift creation forms.
 * <p>
 * Base path: /api/teams
 * Security: Endpoints are protected and require appropriate roles as noted per method.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "Team CRUD Rest API",
        description = "REST APIs - Create Team, Get Team Size, Delete Team, Get Team Members, Get Team Members With Shifts"
)
@Slf4j
@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamServiceImpl teamService;

    @Autowired
    public TeamController(TeamServiceImpl teamService) {
        this.teamService = teamService;
    }

    /**
     * Create a new team in the system.
     * <p>
     * HTTP: POST /api/teams
     * Security: Open endpoint.
     * <p>
     * Note: This endpoint is for development purposes only.
     *
     * @param teamDTO the team data to create
     * @return the created team details
     */

    @Operation(
            summary = "Create Team REST API",
            description = "Create a new team (development purpose only)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created team",
            content = @Content(schema = @Schema(implementation = TeamDTO.class))
    )
    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        log.info("Invoked the POST: createTeam controller method, teamDTO:{}", teamDTO);
        TeamDTO createTeam = teamService.createTeam(teamDTO);
        return new ResponseEntity<>(createTeam, HttpStatus.CREATED);
    }

    /**
     * Retrieve the number of team members for a specific manager.
     * <p>
     * HTTP: GET /api/teams/manager/{managerId}/teamSize
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @return the count of team members
     */

    @Operation(
            summary = "Get Team Size REST API",
            description = "Retrieve the number of team members for a specific manager"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team size",
                    content = @Content(schema = @Schema(implementation = Integer.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/teamSize")
    public ResponseEntity<Integer> getTeamSize(@PathVariable String managerId) {
        log.info("Invoked the GET: getTeamSize controller method, managerId:{}", managerId);
        int teamSize = teamService.getTeamSize(managerId);
        return new ResponseEntity<>(teamSize, HttpStatus.OK);
    }

    /**
     * Permanently remove a team from the system.
     * <p>
     * HTTP: DELETE /api/teams/{teamId}
     * Security: Open endpoint.
     *
     * @param teamId the unique identifier of the team to delete
     * @return empty response
     */

    @Operation(
            summary = "Delete Team REST API",
            description = "Permanently remove a team from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted team"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid team ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("teamId") String teamId) {
        log.info("Invoked the DELETE: deleteTeam controller method, teamId:{}", teamId);
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieve all team members for a specific manager.
     * <p>
     * HTTP: GET /api/teams/manager/{managerId}/team-members
     * Security: Open endpoint (MANAGER role commented out).
     *
     * @param managerId the unique identifier of the manager
     * @return list of team member employee details
     */

    @Operation(
            summary = "Get Team Members REST API",
            description = "Retrieve all team members for a specific manager"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team members",
                    content = @Content(schema = @Schema(implementation = EmployeeDTO[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/manager/{managerId}/team-members")
    public ResponseEntity<List<EmployeeDTO>> getTeamMembers(@PathVariable("managerId") String managerId) {
        log.info("Invoked the GET: getTeamMembers controller method, managerId:{}", managerId);
        List<EmployeeDTO> getTeam = teamService.getTeamMembers(managerId);
        return new ResponseEntity<>(getTeam, HttpStatus.OK);
    }

    /**
     * Retrieve team members with their upcoming shift information.
     * <p>
     * HTTP: GET /api/teams/{employeeId}/members-with-upcoming-shifts
     * Security: Open endpoint.
     * <p>
     * Used for shift swap form display; available for employee usage.
     *
     * @param employeeId the unique identifier of the employee requesting team data
     * @return list of team members with their upcoming shifts
     */

    @Operation(
            summary = "Get Team Members With Upcoming Shifts REST API",
            description = "Retrieve team members with their upcoming shift information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team members with shifts",
                    content = @Content(schema = @Schema(implementation = TeamMembersShiftDTO[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid employee ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{employeeId}/members-with-upcoming-shifts")
    public ResponseEntity<List<TeamMembersShiftDTO>> getTeamMembersWithUpcomingShifts(@PathVariable("employeeId") String employeeId) {
        log.info("Invoked the GET: getTeamMembersWithUpcomingShifts controller method, employeeId:{}", employeeId);
        List<TeamMembersShiftDTO> result = teamService.getTeamMembersWithUpcomingShifts(employeeId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Retrieve team employees formatted for shift creation form.
     * <p>
     * HTTP: GET /api/teams/manager/{managerId}/team-employees
     * Security: Requires MANAGER role.
     *
     * @param managerId the unique identifier of the manager
     * @return list of team employees formatted for shift form display
     */

    @Operation(
            summary = "Get Team Employees For Shift Form REST API",
            description = "Retrieve team employees data formatted for shift creation form"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved team employees for shift form",
                    content = @Content(schema = @Schema(implementation = TeamEmployeesShiftFormResponseDTO[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid manager ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Not Found - Manager not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{managerId}/team-employees")
    public ResponseEntity<List<TeamEmployeesShiftFormResponseDTO>> getTeamEmployeesByManagerInCreateShiftForm(@PathVariable("managerId") String managerId) {
        log.info("Invoked the GET: getTeamEmployeesByManagerInCreateShiftForm controller method, managerId:{}", managerId);
        List<TeamEmployeesShiftFormResponseDTO> getEmployees = teamService.getTeamEmployeesByManagerInCreateShiftForm(managerId);
        return new ResponseEntity<>(getEmployees, HttpStatus.OK);
    }
}
