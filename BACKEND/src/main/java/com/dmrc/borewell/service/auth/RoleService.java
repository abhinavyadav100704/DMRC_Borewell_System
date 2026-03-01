package com.dmrc.borewell.service.auth;

import com.dmrc.borewell.entity.auth.ERole;
import com.dmrc.borewell.entity.auth.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(ERole roleName);
}