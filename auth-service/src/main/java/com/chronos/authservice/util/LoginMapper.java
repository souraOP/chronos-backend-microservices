package com.chronos.authservice.util;


import com.chronos.authservice.dto.GetAllLoginCredentialsDTO;
import com.chronos.authservice.entity.LoginCredential;
import com.chronos.authservice.repository.projections.LoginByEmailView;

public class LoginMapper {
    public static GetAllLoginCredentialsDTO loginEntityToDto(LoginCredential lc) {
        if(lc == null) {
            return null;
        }

        return new GetAllLoginCredentialsDTO(
                lc.getLoginCredentialId(),
                lc.getEmail(),
                lc.getPasswordHash(),
                lc.getDisplayEmployeeId(),
                lc.getRole()
        );
    }

    public static GetAllLoginCredentialsDTO loginViewToDto(LoginByEmailView view) {
        if (view == null) return null;

        return new GetAllLoginCredentialsDTO(
                view.getLoginId(),
                view.getEmail(),
                view.getPasswordHash(),
                view.getDisplayEmployeeId(),
                view.getRole()
        );
    }
}
