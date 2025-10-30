package com.chronos.authservice.repository.projections;

import com.chronos.authservice.constants.enums.Role;

public interface LoginByEmailView {
    String getLoginId();
    String getEmail();
    String getPasswordHash();
    Role getRole();
    String getDisplayEmployeeId();
}
