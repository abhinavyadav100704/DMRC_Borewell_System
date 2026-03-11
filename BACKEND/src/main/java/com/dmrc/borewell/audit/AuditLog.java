package com.dmrc.borewell.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Integer entityId;

    @Column(length = 500)
    private String description;

    private LocalDateTime timestamp;

    private String ipAddress;

    public AuditLog() {}

    public AuditLog(String username, AuditAction action, EntityType entityType,
                    Integer entityId, String description, String ipAddress) {
        this.username = username;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.ipAddress = ipAddress;
    }
}