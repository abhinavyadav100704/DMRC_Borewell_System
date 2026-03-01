package com.dmrc.borewell.service.auth;

import com.dmrc.borewell.entity.auth.ERole;
import com.dmrc.borewell.entity.auth.Role;
import com.dmrc.borewell.repository.auth.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(ERole roleName) {
        return roleRepository.findByName(roleName);
    }
}