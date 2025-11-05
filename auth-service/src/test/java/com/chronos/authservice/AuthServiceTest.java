package com.chronos.authservice;

import com.chronos.authservice.dto.*;
import com.chronos.authservice.entity.LoginCredential;
import com.chronos.authservice.repository.LoginRepository;
import com.chronos.authservice.service.LoginServiceImpl;
import com.chronos.authservice.service.auth.JwtService;
import com.chronos.common.constants.LoginConstants;
import com.chronos.common.constants.enums.Role;
import com.chronos.common.exception.custom.LoginFailedException;
import com.chronos.common.exception.custom.PasswordDoNotMatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginServiceImpl service;


    @Test
    void loginEmployee_success_returnsTokenAndDetails() {
        String email = "user@example.com";
        String rawPwd = "pass";
        LoginDTO dto = new LoginDTO(email, rawPwd);

        Authentication auth = mock(Authentication.class);
        UserDetails principal = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(principal);

        LoginCredential cred = credential(Role.EMPLOYEE);
        when(loginRepository.findByEmailWithoutProjection(email)).thenReturn(Optional.of(cred));
        when(jwtService.generateToken(eq(principal), any(Map.class))).thenReturn("token-123");

        LoginResponseDTO out = service.loginEmployee(dto);

        assertNotNull(out);
        assertEquals(email, out.email());
        assertEquals(Role.EMPLOYEE, out.role());
        assertEquals(cred.getDisplayEmployeeId(), out.employeeId());
        assertEquals("token-123", out.token());
        verify(authenticationManager).authenticate(any());
        verify(loginRepository).findByEmailWithoutProjection(email);
        verify(jwtService).generateToken(eq(principal), any(Map.class));
    }

    @Test
    void loginManager_success_returnsTokenAndDetails() {
        String email = "mgr@example.com";
        LoginDTO dto = new LoginDTO(email, "pass");

        Authentication auth = mock(Authentication.class);
        UserDetails principal = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(principal);

        LoginCredential cred = credential(Role.MANAGER);
        when(loginRepository.findByEmailWithoutProjection(email)).thenReturn(Optional.of(cred));
        when(jwtService.generateToken(eq(principal), any(Map.class))).thenReturn("token-456");

        LoginResponseDTO out = service.loginManager(dto);

        assertEquals(Role.MANAGER, out.role());
        assertEquals("token-456", out.token());
        verify(authenticationManager).authenticate(any());
        verify(loginRepository).findByEmailWithoutProjection(email);
        verify(jwtService).generateToken(eq(principal), any(Map.class));
    }

    @Test
    void loginEmployee_badCredentials_throwsRuntime() {
        String email = "user@example.com";
        LoginDTO dto = new LoginDTO(email, "bad");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThrows(RuntimeException.class, () -> service.loginEmployee(dto));
        verify(authenticationManager).authenticate(any());
        verify(loginRepository, never()).findByEmailWithoutProjection(anyString());
    }

    @Test
    void loginEmployee_repoMissing_throwsLoginFailed() {
        String email = "user@example.com";
        LoginDTO dto = new LoginDTO(email, "pass");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        when(loginRepository.findByEmailWithoutProjection(email)).thenReturn(Optional.empty());

        assertThrows(LoginFailedException.class, () -> service.loginEmployee(dto));
        verify(loginRepository).findByEmailWithoutProjection(email);
    }

    @Test
    void loginEmployee_roleMismatch_throwsRuntime() {
        String email = "user@example.com";
        LoginDTO dto = new LoginDTO(email, "pass");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        LoginCredential cred = credential(Role.MANAGER); //expecting employee over here..
        when(loginRepository.findByEmailWithoutProjection(email)).thenReturn(Optional.of(cred));

        assertThrows(RuntimeException.class, () -> service.loginEmployee(dto));
    }

    @Test
    void getLoginCredentialsByEmail_returnsMappedDto() {
        String email = "login@example.com";
        LoginCredential cred = credential(Role.EMPLOYEE);
        cred.setEmail(email);

        when(loginRepository.findByEmailView(email)).thenReturn(Optional.of(cred));

        GetAllLoginCredentialsDTO out = service.getLoginCredentialsByEmail(email);

        assertNotNull(out);
        verify(loginRepository).findByEmailView(email);
    }

    @Test
    void changePassword_success_updatesAndReturnsMessage() {
        String email = "login@example.com";
        ChangePasswordDTO req = new ChangePasswordDTO("newPass", "newPass");

        LoginCredential cred = credential(Role.EMPLOYEE);
        cred.setEmail(email);

        when(loginRepository.findByEmailWithoutProjection(email)).thenReturn(Optional.of(cred));
        when(passwordEncoder.encode("newPass")).thenReturn("encNew");
        when(loginRepository.save(any(LoginCredential.class))).thenAnswer(inv -> inv.getArgument(0));

        ChangePasswordResponseDTO out = service.changePassword(email, req);

        assertEquals(email, out.email());
        assertEquals(LoginConstants.PASSWORD_CHANGED_SUCCESS, out.message());
        assertEquals("encNew", cred.getPasswordHash());
        verify(loginRepository).save(cred);
    }

    @Test
    void changePassword_mismatch_throwsPasswordDoNotMatch() {
        String email = "login@example.com";
        ChangePasswordDTO req = new ChangePasswordDTO("old", "ab");

        assertThrows(PasswordDoNotMatchException.class, () -> service.changePassword(email, req));
        verify(loginRepository, never()).findByEmailWithoutProjection(anyString());
    }

    private static LoginCredential credential(Role role) {
        LoginCredential c = new LoginCredential();
        c.setId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
        c.setLoginCredentialId("LCRED12345");
        c.setEmail("user@example.com");
        c.setPasswordHash("hash");
        c.setRole(role);
        c.setEmployeeId(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"));
        c.setDisplayEmployeeId("EMP-001");
        return c;
    }
}
