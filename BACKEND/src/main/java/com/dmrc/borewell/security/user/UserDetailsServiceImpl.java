package com.dmrc.borewell.security.user;

import com.dmrc.borewell.entity.auth.User;
import com.dmrc.borewell.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username)
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                // Convert roles to Spring Security SimpleGrantedAuthority by prefix "ROLE_"
                .authorities(user.getRoles().stream()
                        .map(role -> role.getName().name())   // e.g., "ROLE_ADMIN"
                        .toArray(String[]::new)
                )
                .build();
    }
}
