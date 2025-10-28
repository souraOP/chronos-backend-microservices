package com.chronos.authservice.repository.projections;

import com.chronos.authservice.constants.enums.Role;

import java.util.UUID;

public interface LoginByEmailView {
    UUID getLoginId();
    String getEmail();
    String getPasswordHash();
    Role getRole();
    String getDisplayEmployeeId();
}
