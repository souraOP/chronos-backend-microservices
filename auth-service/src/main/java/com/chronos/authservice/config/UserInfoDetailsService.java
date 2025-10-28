package com.chronos.authservice.config;

import com.chronos.authservice.constants.ErrorConstants;
import com.chronos.authservice.entity.LoginCredential;
import com.chronos.authservice.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInfoDetailsService implements UserDetailsService {

    private final LoginRepository loginRepository;

    @Autowired
    public UserInfoDetailsService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginCredential user = loginRepository.findByEmailWithoutProjection(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorConstants.EMPLOYEE_NOT_FOUND + username));
        return new UserInfoDetails(user);
    }
}
