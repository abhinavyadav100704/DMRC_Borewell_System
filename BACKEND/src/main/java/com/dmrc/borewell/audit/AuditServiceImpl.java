package com.dmrc.borewell.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public void logAction(String username, AuditAction action, EntityType entityType,
                          Integer entityId, String description, String ipAddress) {

        // fallback to SecurityContext if username not provided
        if (username == null || username.isEmpty()) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            username = principal.toString(); // can refine if using UserDetails
        }

        AuditLog log = new AuditLog(username, action, entityType, entityId, description, ipAddress);
        auditLogRepository.save(log);
    }

    @Override
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> getLogsByUser(String username) {
        return auditLogRepository.findByUsername(username);
    }

    @Override
    public List<AuditLog> getLogsByEntity(EntityType entityType) {
        return auditLogRepository.findByEntityType(entityType);
    }
}