package com.chronos.authservice.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.chronos.authservice.dto.*;
import com.chronos.authservice.entity.LoginCredential;
import com.chronos.authservice.feign.EmployeeClient;
import com.chronos.authservice.repository.LoginRepository;
import com.chronos.authservice.service.auth.JwtService;
import com.chronos.authservice.service.interfaces.LoginService;
import com.chronos.authservice.util.LoginMapper;
import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.LoginConstants;
import com.chronos.common.constants.enums.Role;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.LoginFailedException;
import com.chronos.common.exception.custom.PasswordDoNotMatchException;
import com.chronos.common.exception.custom.ResourceNotFoundException;
import com.chronos.common.util.NanoIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {
    private final LoginRepository loginRepository;
    private final EmployeeClient employeeClient;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginServiceImpl(LoginRepository loginRepository, EmployeeClient employeeClient, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.loginRepository = loginRepository;
        this.employeeClient = employeeClient;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public LoginResponseDTO loginEmployee(LoginDTO loginDTO) {
        log.info("Invoked the loginEmployee service method, loginDTO:{}", loginDTO);
        return doLogin(loginDTO, Role.EMPLOYEE, LoginConstants.EMPLOYEE_LOGIN_SUCCESS, ErrorConstants.EMPLOYEE_LOGIN_FAILED);
    }

    @Override
    public LoginResponseDTO loginManager(LoginDTO loginDTO) {
        log.info("Invoked the loginManager service method, loginDTO:{}", loginDTO);
        return doLogin(loginDTO, Role.MANAGER, LoginConstants.MANAGER_LOGIN_SUCCESS, ErrorConstants.MANAGER_LOGIN_FAILED);
    }

    private LoginResponseDTO doLogin(LoginDTO loginDTO, Role role, String successMessage, String errorMessage) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()
                    ));
        } catch (BadCredentialsException e) {
            throw new LoginFailedException(errorMessage + e);
        }


        LoginCredential credential = loginRepository.findByEmailWithoutProjection(loginDTO.email())
                .orElseThrow(() -> new LoginFailedException(ErrorConstants.EMPLOYEE_LOGIN_FAILED));

        if (role != credential.getRole()) {
            throw new LoginFailedException(errorMessage);
        }

        String employeeID = credential.getDisplayEmployeeId();
        UUID employeeUUID = credential.getEmployeeId();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", credential.getRole().name());
        claims.put("uuid", employeeUUID.toString());
        claims.put("employeeId", employeeID);

        String token = jwtService.generateToken(userDetails, claims);

        return new LoginResponseDTO(employeeUUID, credential.getEmail(), credential.getRole(), successMessage, employeeID, token);
    }


    @Override
    @Transactional
    public CreateLoginCredentialResponseDTO createLoginCredentials(CreateLoginCredentialDTO createLoginCredentialDTO) {
        log.info("Invoked the createLoginCredentials service method, createLoginCredentialDTO:{}", createLoginCredentialDTO);
        EmployeeDTO employee = employeeClient.getEmployeeByDisplayId(createLoginCredentialDTO.employeeId());

        LoginCredential loginCredential = new LoginCredential();

        int loginCredentialIdLength = 10;
        String nanoId = NanoIdUtils.randomNanoId(
                NanoIdGenerator.DEFAULT_NUMBER_GENERATOR,
                NanoIdGenerator.DEFAULT_ALPHABET,
                loginCredentialIdLength
        );

        loginCredential.setLoginCredentialId(nanoId);
        loginCredential.setEmail(createLoginCredentialDTO.email());
        loginCredential.setPasswordHash(passwordEncoder.encode(createLoginCredentialDTO.password()));
        loginCredential.setRole(createLoginCredentialDTO.role());
        loginCredential.setEmployeeId(employee.id());
        loginCredential.setDisplayEmployeeId(employee.displayEmployeeId());

        loginRepository.save(loginCredential);

        return new CreateLoginCredentialResponseDTO(loginCredential.getLoginCredentialId(), LoginConstants.LOGIN_DETAILS_CREATED, loginCredential.getDisplayEmployeeId());
    }

    @Override
    public GetAllLoginCredentialsDTO getLoginCredentialsByEmail(String email) {
        log.info("Invoked the getLoginCredentialsByEmail service method, email:{}", email);
        LoginCredential loginCredential = loginRepository.findByEmailView(email)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.LOGIN_CREDENTIALS_NOT_FOUND + email));

        return LoginMapper.loginEntityToDto(loginCredential);
    }

    @Override
    @Transactional
    public ChangePasswordResponseDTO changePassword(String email, ChangePasswordDTO changePasswordDTO) {
        log.info("Invoked the changePassword service method, email:{}. changePasswordDTO:{}", email, changePasswordDTO);
        if (!changePasswordDTO.newPassword().equals(changePasswordDTO.confirmPassword())) {
            throw new PasswordDoNotMatchException(ErrorConstants.NEW_PASSWORD_CONFIRM_PASSWORD_NOT_MATCH);
        }
        LoginCredential loginCredential = loginRepository.findByEmailWithoutProjection(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.LOGIN_CREDENTIALS_NOT_FOUND + email));

        loginCredential.setPasswordHash(passwordEncoder.encode(changePasswordDTO.confirmPassword()));
        loginRepository.save(loginCredential);

        String message = LoginConstants.PASSWORD_CHANGED_SUCCESS;
        return new ChangePasswordResponseDTO(email, message);
    }
}
