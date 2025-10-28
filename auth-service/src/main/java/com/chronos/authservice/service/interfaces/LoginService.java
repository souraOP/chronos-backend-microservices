package com.chronos.authservice.service.interfaces;

import com.chronos.authservice.dto.*;

public interface LoginService {
    LoginResponseDTO loginEmployee(LoginDTO loginDTO);

    LoginResponseDTO loginManager(LoginDTO loginDTO);

    CreateLoginCredentialResponseDTO createLoginCredentials(CreateLoginCredentialDTO createLoginCredentialDTO);

//    List<GetAllLoginCredentialsDTO> getAllLoginCredentials();

//    GetAllLoginCredentialsDTO getLoginCredentialsByEmail(String email);

    ChangePasswordResponseDTO changePassword(String email, ChangePasswordDTO newPassword);
}
