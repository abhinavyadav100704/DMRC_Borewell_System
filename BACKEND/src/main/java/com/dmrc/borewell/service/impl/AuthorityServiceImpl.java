package com.dmrc.borewell.service.impl;

import com.dmrc.borewell.audit.AuditAction;
import com.dmrc.borewell.audit.AuditService;
import com.dmrc.borewell.audit.EntityType;
import com.dmrc.borewell.entity.Authority;
import com.dmrc.borewell.exception.BadRequestException;
import com.dmrc.borewell.exception.ResourceNotFoundException;
import com.dmrc.borewell.repository.AuthorityRepository;
import com.dmrc.borewell.repository.BorewellRepository;
import com.dmrc.borewell.service.AuthorityService;
import com.dmrc.borewell.notification.NotificationService;
import com.dmrc.borewell.notification.NotificationSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private BorewellRepository borewellRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Authority save(Authority authority) {
        Authority saved = authorityRepository.save(authority);

        // ✅ Audit log for create
        auditService.logAction(
                null,
                AuditAction.CREATE,
                EntityType.AUTHORITY,
                saved.getAuthorityId(),
                "Created authority: " + saved.getName(),
                null
        );

        // ✅ Notification for creation
        notificationService.createNotification(
                "Authority Created",
                "Authority \"" + saved.getName() + "\" has been created.",
                NotificationSeverity.INFO,
                null
        );

        return saved;
    }

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    @Override
    public Optional<Authority> findById(Integer id) {
        return authorityRepository.findById(id);
    }

    // ✅ UPDATE with auditing and notifications
    @Override
    public Authority update(Integer id, Authority authorityDetails) {

        Authority existing = authorityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authority not found with id: " + id)
                );

        existing.setName(authorityDetails.getName());
        existing.setDesignation(authorityDetails.getDesignation());
        existing.setContactNumber(authorityDetails.getContactNumber());
        existing.setEmail(authorityDetails.getEmail());

        Authority updated = authorityRepository.save(existing);

        // ✅ Audit log for update
        auditService.logAction(
                null,
                AuditAction.UPDATE,
                EntityType.AUTHORITY,
                updated.getAuthorityId(),
                "Updated authority: " + updated.getName(),
                null
        );

        // ✅ Notification for update
        notificationService.createNotification(
                "Authority Updated",
                "Authority \"" + updated.getName() + "\" has been updated.",
                NotificationSeverity.INFO,
                null
        );

        return updated;
    }

    // ✅ DELETE with validation, auditing, and notifications
    @Override
    public void delete(Integer id) {

        Authority authority = authorityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authority not found with id: " + id)
                );

        boolean isUsed = borewellRepository.existsByAuthorityAuthorityId(id);

        if (isUsed) {
            throw new BadRequestException(
                    "Cannot delete authority. It is assigned to existing borewells."
            );
        }

        authorityRepository.delete(authority);

        // ✅ Audit log for delete
        auditService.logAction(
                null,
                AuditAction.DELETE,
                EntityType.AUTHORITY,
                authority.getAuthorityId(),
                "Deleted authority: " + authority.getName(),
                null
        );

        // ✅ Notification for delete
        notificationService.createNotification(
                "Authority Deleted",
                "Authority \"" + authority.getName() + "\" has been deleted.",
                NotificationSeverity.WARNING,
                null
        );
    }
}