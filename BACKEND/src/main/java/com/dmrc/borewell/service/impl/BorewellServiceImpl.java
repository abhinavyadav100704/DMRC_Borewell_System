package com.dmrc.borewell.service.impl;

import com.dmrc.borewell.audit.AuditAction;
import com.dmrc.borewell.audit.AuditService;
import com.dmrc.borewell.audit.EntityType;
import com.dmrc.borewell.entity.Authority;
import com.dmrc.borewell.entity.Borewell;
import com.dmrc.borewell.entity.Station;
import com.dmrc.borewell.exception.BadRequestException;
import com.dmrc.borewell.exception.ResourceNotFoundException;
import com.dmrc.borewell.repository.BorewellRepository;
import com.dmrc.borewell.service.AuthorityService;
import com.dmrc.borewell.service.BorewellService;
import com.dmrc.borewell.notification.NotificationService;
import com.dmrc.borewell.service.StationService;
import com.dmrc.borewell.notification.NotificationSeverity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BorewellServiceImpl implements BorewellService {

    @Autowired
    private BorewellRepository borewellRepository;

    @Autowired
    private StationService stationService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private NotificationService notificationService;

    // CREATE
    @Override
    public Borewell create(Borewell borewell) {

        if (borewell.getStation() == null) {
            throw new BadRequestException("Station is required");
        }

        Station station = stationService.findById(
                borewell.getStation().getStationId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Station not found with id: "
                        + borewell.getStation().getStationId())
        );

        borewell.setStation(station);

        if (borewell.getAuthority() != null) {

            Authority authority = authorityService.findById(
                    borewell.getAuthority().getAuthorityId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Authority not found with id: "
                            + borewell.getAuthority().getAuthorityId())
            );

            borewell.setAuthority(authority);
        }

        Borewell saved = borewellRepository.save(borewell);

        // ✅ Audit log for creation
        auditService.logAction(
                null,
                AuditAction.CREATE,
                EntityType.BOREWELL,
                saved.getBorewellId(),
                "Created borewell with borewellNo: " + saved.getBorewellNo(),
                null
        );

        // ✅ Notification for creation
        notificationService.createNotification(
                "Borewell Created",
                "Borewell #" + saved.getBorewellNo() + " has been created.",
                NotificationSeverity.INFO,
                null
        );

        return saved;
    }

    @Override
    public List<Borewell> findAll() {
        return borewellRepository.findAll();
    }

    @Override
    public Optional<Borewell> findById(Integer id) {
        return borewellRepository.findById(id);
    }

    // UPDATE
    @Override
    public Borewell update(Integer id, Borewell borewellDetails) {

        Borewell existing = borewellRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Borewell not found with id: " + id)
                );

        existing.setBorewellNo(borewellDetails.getBorewellNo());
        existing.setIsAvailable(borewellDetails.getIsAvailable());
        existing.setDistanceM(borewellDetails.getDistanceM());
        existing.setDiameter(borewellDetails.getDiameter());
        existing.setDepth(borewellDetails.getDepth());
        existing.setLocation(borewellDetails.getLocation());
        existing.setApprovalDate(borewellDetails.getApprovalDate());

        if (borewellDetails.getStation() != null) {
            Station station = stationService.findById(
                    borewellDetails.getStation().getStationId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Station not found with id: "
                            + borewellDetails.getStation().getStationId())
            );
            existing.setStation(station);
        }

        if (borewellDetails.getAuthority() != null) {
            Authority authority = authorityService.findById(
                    borewellDetails.getAuthority().getAuthorityId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Authority not found with id: "
                            + borewellDetails.getAuthority().getAuthorityId())
            );
            existing.setAuthority(authority);
        }

        Borewell updated = borewellRepository.save(existing);

        // ✅ Audit log for update
        auditService.logAction(
                null,
                AuditAction.UPDATE,
                EntityType.BOREWELL,
                updated.getBorewellId(),
                "Updated borewell with borewellNo: " + updated.getBorewellNo(),
                null
        );

        // ✅ Notification for update
        notificationService.createNotification(
                "Borewell Updated",
                "Borewell #" + updated.getBorewellNo() + " has been updated.",
                NotificationSeverity.INFO,
                null
        );

        return updated;
    }

    // DELETE
    @Override
    public void delete(Integer id) {

        Borewell borewell = borewellRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Borewell not found with id: " + id)
                );

        borewellRepository.delete(borewell);

        // ✅ Audit log for delete
        auditService.logAction(
                null,
                AuditAction.DELETE,
                EntityType.BOREWELL,
                borewell.getBorewellId(),
                "Deleted borewell with borewellNo: " + borewell.getBorewellNo(),
                null
        );

        // ✅ Notification for delete
        notificationService.createNotification(
                "Borewell Deleted",
                "Borewell #" + borewell.getBorewellNo() + " has been deleted.",
                NotificationSeverity.WARNING,
                null
        );
    }
}