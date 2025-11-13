package com.chronos.authservice;

import com.chronos.authservice.controller.LoginController;
import com.chronos.authservice.dto.*;
import com.chronos.authservice.service.LoginServiceImpl;
import com.chronos.common.constants.LoginConstants;
import com.chronos.common.constants.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LoginServiceImpl loginService;

    @MockitoBean(name = "jpaMappingContext")
    JpaMetamodelMappingContext jpaMappingContext;

    @TestConfiguration
    static class NoopAuditorConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("test-user");
        }
    }

    @Test
    void loginEmployee_returns200AndBody() throws Exception {
        String email = "emp@example.com";
        LoginDTO req = new LoginDTO(email, "pass");
        UUID uuid = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        LoginResponseDTO resp = new LoginResponseDTO(
                uuid, email, Role.EMPLOYEE, LoginConstants.EMPLOYEE_LOGIN_SUCCESS, "EMP-001", "jwt-emp"
        );

        when(loginService.loginEmployee(any(LoginDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/login/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"))
                .andExpect(jsonPath("$.message").value(LoginConstants.EMPLOYEE_LOGIN_SUCCESS))
                .andExpect(jsonPath("$.employeeId").value("EMP-001"))
                .andExpect(jsonPath("$.token").value("jwt-emp"));

        verify(loginService).loginEmployee(eq(req));
    }

    @Test
    void loginManager_returns200AndBody() throws Exception {
        String email = "mgr@example.com";
        LoginDTO req = new LoginDTO(email, "pass");
        UUID uuid = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        LoginResponseDTO resp = new LoginResponseDTO(
                uuid, email, Role.MANAGER, LoginConstants.MANAGER_LOGIN_SUCCESS, "EMP-900", "jwt-mgr"
        );

        when(loginService.loginManager(any(LoginDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/login/manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.message").value(LoginConstants.MANAGER_LOGIN_SUCCESS))
                .andExpect(jsonPath("$.employeeId").value("EMP-900"))
                .andExpect(jsonPath("$.token").value("jwt-mgr"));

        verify(loginService).loginManager(eq(req));
    }

    @Test
    void createLoginDetails_returns200AndBody() throws Exception {
        CreateLoginCredentialDTO req = new CreateLoginCredentialDTO(
                "newuser@example.com", "secret", "EMP-001", Role.EMPLOYEE
        );
        CreateLoginCredentialResponseDTO resp = new CreateLoginCredentialResponseDTO(
                "LCRED12345", LoginConstants.LOGIN_DETAILS_CREATED, "EMP-001"
        );

        when(loginService.createLoginCredentials(any(CreateLoginCredentialDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginCredentialId").value("LCRED12345"))
                .andExpect(jsonPath("$.message").value(LoginConstants.LOGIN_DETAILS_CREATED))
                .andExpect(jsonPath("$.employeeId").value("EMP-001"));

        verify(loginService).createLoginCredentials(eq(req));
    }

    @Test
    void getLoginCredentialByEmail_returns200() throws Exception {
        String email = "user@example.com";

        when(loginService.getLoginCredentialsByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/login/{email}", email))
                .andExpect(status().isOk());

        verify(loginService).getLoginCredentialsByEmail(eq(email));
    }

    @Test
    void changePassword_returns200AndBody() throws Exception {
        String email = "user@example.com";
        ChangePasswordDTO req = new ChangePasswordDTO("newPass123", "newPass123");
        ChangePasswordResponseDTO resp = new ChangePasswordResponseDTO(email, LoginConstants.PASSWORD_CHANGED_SUCCESS);

        when(loginService.changePassword(eq(email), any(ChangePasswordDTO.class))).thenReturn(resp);

        mockMvc.perform(patch("/api/login/change-password")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").value(LoginConstants.PASSWORD_CHANGED_SUCCESS));

        verify(loginService).changePassword(eq(email), eq(req));
    }
}
