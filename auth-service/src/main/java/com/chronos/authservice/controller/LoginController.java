package com.chronos.authservice.controller;

import com.chronos.authservice.dto.*;
import com.chronos.authservice.service.LoginServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@CrossOrigin("*")
public class LoginController {
    private final LoginServiceImpl loginService;

    @Autowired
    public LoginController(LoginServiceImpl loginService
    ) {
        this.loginService = loginService;
    }

    // AUTHENTICATE EMPLOYEE LOGIN!!!, email, passwordHash, role=EMPLOYEE
    @PostMapping("/employee")
    public ResponseEntity<LoginResponseDTO> loginEmployee(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO loginEmp = loginService.loginEmployee(loginDTO);
        return ResponseEntity.ok(loginEmp);
    }

    // AUTHENTICATE MANAGER LOGIN!!!, email, passwordHash, role=manager
    @PostMapping("/manager")
    public ResponseEntity<LoginResponseDTO> loginManager(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO loginMng = loginService.loginManager(loginDTO);
        return ResponseEntity.ok(loginMng);
    }

    // creating login credentials
    // this is only for development purpose
    // not present in the srs document
    @PostMapping
    public ResponseEntity<CreateLoginCredentialResponseDTO> createLoginDetails(@Valid @RequestBody CreateLoginCredentialDTO createLoginCredentialDTO) {
        CreateLoginCredentialResponseDTO savedLoginDetails = loginService.createLoginCredentials(createLoginCredentialDTO);
        return ResponseEntity.ok(savedLoginDetails);
    }

    // fetching all the login credentials from the database
//    @GetMapping
//    public ResponseEntity<List<GetAllLoginCredentialsDTO>> getAllLoginCredentials() {
//        List<GetAllLoginCredentialsDTO> getAllLoginDetails = loginService.getAllLoginCredentials();
//        return ResponseEntity.ok(getAllLoginDetails);
//    }
//
//    // fetching login credentials by their employeeid
//    @GetMapping("/{email}")
//    public ResponseEntity<GetAllLoginCredentialsDTO> getLoginCredentialById(@PathVariable String email) {
//        GetAllLoginCredentialsDTO getLoginDetailsByEmail = loginService.getLoginCredentialsByEmail(email);
//        return new ResponseEntity<>(getLoginDetailsByEmail, HttpStatus.OK);
//    }
//
//    // changing the passwordHash
//    // patch request for changing the passwordHash
    @PatchMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(@RequestParam("email") String email, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        ChangePasswordResponseDTO changedPassword = loginService.changePassword(email, changePasswordDTO);
        return ResponseEntity.ok(changedPassword);
    }

}
