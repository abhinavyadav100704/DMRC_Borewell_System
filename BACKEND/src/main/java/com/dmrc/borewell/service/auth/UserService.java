package com.dmrc.borewell.service.auth;

import com.dmrc.borewell.entity.auth.User;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
