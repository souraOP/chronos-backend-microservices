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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Login CRUD Rest API",
        description = "REST APIs - Employee Login, Manager Login, Login Credential Management, Password Management"
)
@RestController
@RequestMapping("/api/login")
public class LoginController {
    private final LoginServiceImpl loginService;

    @Autowired
    public LoginController(LoginServiceImpl loginService
    ) {
        this.loginService = loginService;
    }



    // AUTHENTICATE EMPLOYEE LOGIN!!!, email, passwordHash, role=EMPLOYEE
    @Operation(
            summary = "Authenticate Employee Login REST API",
            description = "Validate employee credentials and return authentication token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid login credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/employee")
    public ResponseEntity<LoginResponseDTO> loginEmployee(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO loginEmp = loginService.loginEmployee(loginDTO);
        return ResponseEntity.ok(loginEmp);
    }





    // AUTHENTICATE MANAGER LOGIN!!!, email, passwordHash, role=manager
    @Operation(
            summary = "Authenticate Manager Login REST API",
            description = "Validate manager credentials and return authentication token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid login credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/manager")
    public ResponseEntity<LoginResponseDTO> loginManager(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO loginMng = loginService.loginManager(loginDTO);
        return ResponseEntity.ok(loginMng);
    }




    // creating login credentials
    // this is only for development purpose
    // not present in the srs document
    @Operation(
            summary = "Create Login Credential REST API",
            description = "Create new login credentials (development purpose only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created login credentials",
                    content = @Content(schema = @Schema(implementation = CreateLoginCredentialResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CreateLoginCredentialResponseDTO> createLoginDetails(@Valid @RequestBody CreateLoginCredentialDTO createLoginCredentialDTO) {
        CreateLoginCredentialResponseDTO savedLoginDetails = loginService.createLoginCredentials(createLoginCredentialDTO);
        return ResponseEntity.ok(savedLoginDetails);
    }




    // fetching login credentials by their employeeid
    @Operation(
            summary = "Get Login Credential By Email REST API",
            description = "Retrieve login credentials for a specific email address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved login credentials",
                    content = @Content(schema = @Schema(implementation = GetAllLoginCredentialsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid email format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{email}")
    public ResponseEntity<GetAllLoginCredentialsDTO> getLoginCredentialById(@PathVariable String email) {
        GetAllLoginCredentialsDTO getLoginDetailsByEmail = loginService.getLoginCredentialsByEmail(email);
        return new ResponseEntity<>(getLoginDetailsByEmail, HttpStatus.OK);
    }





 // changing the passwordHash
  // patch request for changing the passwordHash
     @Operation(
             summary = "Change Password REST API",
             description = "Update password for existing login credentials"
     )
     @ApiResponses(value = {
             @ApiResponse(responseCode = "200", description = "Successfully changed password",
                     content = @Content(schema = @Schema(implementation = ChangePasswordResponseDTO.class))),
             @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or email format",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
             @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
             @ApiResponse(responseCode = "404", description = "Not Found - Login credentials not found for email",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
             @ApiResponse(responseCode = "500", description = "Internal Server Error",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
     })
    @PatchMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(@RequestParam("email") String email, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        ChangePasswordResponseDTO changedPassword = loginService.changePassword(email, changePasswordDTO);
        return ResponseEntity.ok(changedPassword);
    }

}
