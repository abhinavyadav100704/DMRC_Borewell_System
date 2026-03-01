package com.dmrc.borewell.controller.auth;

import com.dmrc.borewell.entity.auth.ERole;
import com.dmrc.borewell.entity.auth.Role;
import com.dmrc.borewell.entity.auth.User;
import com.dmrc.borewell.exception.BadRequestException;
import com.dmrc.borewell.exception.ResourceNotFoundException;
import com.dmrc.borewell.repository.auth.RoleRepository;
import com.dmrc.borewell.security.auth.JwtResponse;
import com.dmrc.borewell.security.auth.LoginRequest;
import com.dmrc.borewell.security.auth.SignupRequest;
import com.dmrc.borewell.security.jwt.JwtUtils;
import com.dmrc.borewell.service.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {

        if (userService.existsByUsername(signupRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword()
        );

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if ("admin".equalsIgnoreCase(signupRequest.getRole())) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new BadRequestException("Admin role not found"));
            roles.add(adminRole);
        } else {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new BadRequestException("User role not found"));
            roles.add(userRole);
        }
        user.setRoles(roles);

        userService.saveUser(user);

        return ResponseEntity.ok(
                Map.of("message", "User registered successfully")
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService
                .findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        roles
                )
        );
    }
}