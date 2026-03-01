package com.dmrc.borewell.controller;

import com.dmrc.borewell.entity.Authority;
import com.dmrc.borewell.exception.ResourceNotFoundException;
import com.dmrc.borewell.service.AuthorityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorities")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    // ✅ user and admin can create authority
    @PostMapping
    public ResponseEntity<Authority> createAuthority(@RequestBody Authority authority) {
        Authority savedAuthority = authorityService.save(authority);
        return ResponseEntity.ok(savedAuthority);
    }

    // ✅ USER + ADMIN can view all
    @GetMapping
    public List<Authority> getAllAuthorities() {
        return authorityService.findAll();
    }

    // ✅ USER + ADMIN can view by ID
    @GetMapping("/{id}")
    public ResponseEntity<Authority> getAuthorityById(@PathVariable Integer id) {

        Authority authority = authorityService.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authority not found with id: " + id)
                );

        return ResponseEntity.ok(authority);
    }

    // ✅ Only ADMIN can update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Authority> updateAuthority(@PathVariable Integer id,
                                                     @RequestBody Authority authorityDetails) {

        Authority existing = authorityService.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authority not found with id: " + id)
                );

        // Update all fields
        existing.setName(authorityDetails.getName());
        existing.setDesignation(authorityDetails.getDesignation());
        existing.setContactNumber(authorityDetails.getContactNumber());
        existing.setEmail(authorityDetails.getEmail());

        Authority updated = authorityService.save(existing);

        return ResponseEntity.ok(updated);
    }

    // ✅ Only ADMIN can delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthority(@PathVariable Integer id) {

        Authority authority = authorityService.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authority not found with id: " + id)
                );

        authorityService.deleteById(authority.getAuthorityId());

        return ResponseEntity.noContent().build();
    }
}
