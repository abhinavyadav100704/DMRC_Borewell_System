package com.dmrc.borewell.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditService.getAllLogs();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{username}")
    public List<AuditLog> getLogsByUser(@PathVariable String username) {
        return auditService.getLogsByUser(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/entity/{entityType}")
    public List<AuditLog> getLogsByEntity(@PathVariable EntityType entityType) {
        return auditService.getLogsByEntity(entityType);
    }
}