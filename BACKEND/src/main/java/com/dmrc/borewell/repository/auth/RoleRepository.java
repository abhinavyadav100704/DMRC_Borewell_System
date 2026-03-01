package com.dmrc.borewell.repository.auth;

import com.dmrc.borewell.entity.auth.ERole;
import com.dmrc.borewell.entity.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}