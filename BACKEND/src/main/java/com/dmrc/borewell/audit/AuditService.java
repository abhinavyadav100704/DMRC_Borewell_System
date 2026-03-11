package com.dmrc.borewell.audit;

import java.util.List;

public interface AuditService {

    void logAction(String username, AuditAction action, EntityType entityType,
                   Integer entityId, String description, String ipAddress);

    List<AuditLog> getAllLogs();

    List<AuditLog> getLogsByUser(String username);

    List<AuditLog> getLogsByEntity(EntityType entityType);
}