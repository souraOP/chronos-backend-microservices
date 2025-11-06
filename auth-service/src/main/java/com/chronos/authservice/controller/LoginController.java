package com.chronos.authservice.controller;

import com.chronos.authservice.dto.*;
import com.chronos.authservice.service.LoginServiceImpl;
import com.chronos.common.exception.ErrorResponse;
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
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that manages authentication and login credential operations.
 * <p>
 * Responsibilities:
 * - Authenticate employee login requests and issue JWT tokens.
 * - Authenticate manager login requests and issue JWT tokens.
 * - Create login credentials for new users (development purpose).
 * - Retrieve login credentials by email address.
 * <p>
 * Base path: /api/login
 * Security: Open endpoints for authentication; credential management for development use.
 * <p>
 * Created by: Sourasish Mondal
 * Since: 2025-11-06
 */

@Tag(
        name = "Login CRUD Rest API",
        description = "REST APIs - Employee Login, Manager Login, Login Credential Management, Password Management"
)
@Slf4j
@RestController
@RequestMapping("/api/login")
public class LoginController {
    private final LoginServiceImpl loginService;

    @Autowired
    public LoginController(LoginServiceImpl loginService) {
        this.loginService = loginService;
    }

    /**
     * Authenticate an employee login request and issue a JWT token.
     * <p>
     * HTTP: POST /api/login/employee
     * Security: Open endpoint for authentication.
     * <p>
     * This endpoint validates employee credentials (email and password) against the database.
     * Upon successful authentication, it returns a JWT token that can be used for subsequent
     * API calls requiring EMPLOYEE role authorization. The password is validated using bcrypt
     * hashing for security.
     *
     * @param loginDTO the login credentials containing email and password
     * @return ResponseEntity containing authentication token, user details, and HTTP 200 status
     */

    @Operation(
            summary = "Authenticate Employee Login REST API",
            description = "Validate employee credentials and return authentication token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid login credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/employee")
    public ResponseEntity<LoginResponseDTO> loginEmployee(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("Invoked the POST: loginEmployee controller method, loginDTO:{}", loginDTO);
        LoginResponseDTO loginEmp = loginService.loginEmployee(loginDTO);
        return ResponseEntity.ok(loginEmp);
    }

    /**
     * Authenticate a manager login request and issue a JWT token.
     * <p>
     * HTTP: POST /api/login/manager
     * Security: Open endpoint for authentication.
     * <p>
     * This endpoint validates manager credentials (email and password) against the database.
     * Upon successful authentication, it returns a JWT token with MANAGER role that provides
     * access to management endpoints such as team management, shift assignment, leave approval,
     * and reporting features. Password validation uses bcrypt hashing.
     *
     * @param loginDTO the login credentials containing email and password
     * @return LoginResponseDTO containing authentication token, user details, and HTTP 200 status
     */

    @Operation(
            summary = "Authenticate Manager Login REST API",
            description = "Validate manager credentials and return authentication token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid login credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/manager")
    public ResponseEntity<LoginResponseDTO> loginManager(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("Invoked the POST: loginManager controller method, loginDTO:{}", loginDTO);
        LoginResponseDTO loginMng = loginService.loginManager(loginDTO);
        return ResponseEntity.ok(loginMng);
    }

    /**
     * Create new login credentials for a user.
     * <p>
     * HTTP: POST /api/login
     * Security: Open endpoint.
     * <p>
     * <strong>Note:</strong> This endpoint is for development purposes only and is not part of
     * the Software Requirements Specification (SRS) document. In production, user credentials
     * should be created through a secure administrative interface with proper authorization.
     * <p>
     * The password provided in the request is automatically hashed using bcrypt before storage.
     * The role (EMPLOYEE/MANAGER) must be specified in the request.
     *
     * @param createLoginCredentialDTO the credential data including email, password, and role
     * @return ResponseEntity containing the created login credential details and HTTP 200 status
     */

    @Operation(
            summary = "Create Login Credential REST API",
            description = "Create new login credentials (development purpose only)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully created login credentials",
                    content = @Content(schema = @Schema(implementation = CreateLoginCredentialResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<CreateLoginCredentialResponseDTO> createLoginDetails(@Valid @RequestBody CreateLoginCredentialDTO createLoginCredentialDTO) {
        log.info("Invoked the POST: createLoginDetails controller method, createLoginCredentialDTO:{}", createLoginCredentialDTO);
        CreateLoginCredentialResponseDTO savedLoginDetails = loginService.createLoginCredentials(createLoginCredentialDTO);
        return ResponseEntity.ok(savedLoginDetails);
    }

    /**
     * Retrieve login credentials for a specific email address.
     * <p>
     * HTTP: GET /api/login/{email}
     * Security: Open endpoint.
     * <p>
     * This endpoint fetches the stored login credentials associated with an email address.
     * <strong>Security Warning:</strong> The password hash is included in the response for
     * development/testing purposes. In production, this endpoint should either be removed
     * or modified to exclude sensitive information like password hashes.
     *
     * @param email the email address to fetch login credentials for
     * @return ResponseEntity containing the login credentials and HTTP 200 status
     */

    @Operation(
            summary = "Get Login Credential By Email REST API",
            description = "Retrieve login credentials for a specific email address"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved login credentials",
                    content = @Content(schema = @Schema(implementation = GetAllLoginCredentialsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid email format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{email}")
    public ResponseEntity<GetAllLoginCredentialsDTO> getLoginCredentialById(@PathVariable String email) {
        log.info("Invoked the GET: getLoginCredentialById controller method, email:{}", email);
        GetAllLoginCredentialsDTO getLoginDetailsByEmail = loginService.getLoginCredentialsByEmail(email);
        return new ResponseEntity<>(getLoginDetailsByEmail, HttpStatus.OK);
    }

    /**
     * Update the password for existing login credentials.
     * <p>
     * HTTP: PATCH /api/login/change-password?email={email}
     * <p>
     * This endpoint allows users to change their password by providing their email address
     * and the new password. The new password is automatically hashed using bcrypt before
     * being stored in the database, ensuring secure password storage.
     *
     * @param email             the email address of the user changing their password
     * @param changePasswordDTO the new password details
     * @return ResponseEntity containing confirmation of password change and HTTP 200 status
     */

    @Operation(
            summary = "Change Password REST API",
            description = "Update password for existing login credentials"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully changed password",
                    content = @Content(schema = @Schema(implementation = ChangePasswordResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data or email format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Login credentials not found for email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(@RequestParam("email") String email, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("Invoked the PATCH: changePassword controller method, email:{}, changePasswordDTO:{}", email, changePasswordDTO);
        ChangePasswordResponseDTO changedPassword = loginService.changePassword(email, changePasswordDTO);
        return ResponseEntity.ok(changedPassword);
    }
}
