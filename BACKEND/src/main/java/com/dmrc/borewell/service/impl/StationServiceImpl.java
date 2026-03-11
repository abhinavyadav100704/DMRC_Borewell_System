package com.dmrc.borewell.service.impl;

import com.dmrc.borewell.audit.AuditAction;
import com.dmrc.borewell.audit.AuditService;
import com.dmrc.borewell.audit.EntityType;
import com.dmrc.borewell.entity.Station;
import com.dmrc.borewell.exception.BadRequestException;
import com.dmrc.borewell.exception.ResourceNotFoundException;
import com.dmrc.borewell.repository.BorewellRepository;
import com.dmrc.borewell.repository.StationRepository;
import com.dmrc.borewell.service.StationService;
import com.dmrc.borewell.notification.NotificationService;
import com.dmrc.borewell.notification.NotificationSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private BorewellRepository borewellRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private NotificationService notificationService;

    // CREATE / SAVE
    @Override
    public Station save(Station station) {
        Station saved = stationRepository.save(station);

        // ✅ Audit log for create
        auditService.logAction(
                null,
                AuditAction.CREATE,
                EntityType.STATION,
                saved.getStationId(),
                "Created station: " + saved.getStationName(),
                null
        );

        // ✅ Notification for creation
        notificationService.createNotification(
                "Station Created",
                "Station \"" + saved.getStationName() + "\" has been created.",
                NotificationSeverity.INFO,
                null
        );

        return saved;
    }

    // READ ALL
    @Override
    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    // READ BY ID
    @Override
    public Optional<Station> findById(Integer id) {
        return stationRepository.findById(id);
    }

    // UPDATE
    @Override
    public Station update(Integer id, Station stationDetails) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Station not found with id: " + id)
                );

        station.setStationName(stationDetails.getStationName());
        station.setLineId(stationDetails.getLineId());
        station.setLocation(stationDetails.getLocation());
        station.setPlatformCount(stationDetails.getPlatformCount());
        station.setOpeningDate(stationDetails.getOpeningDate());
        station.setStationType(stationDetails.getStationType());
        station.setLastMaintenanceDate(stationDetails.getLastMaintenanceDate());
        station.setMaintenanceNotes(stationDetails.getMaintenanceNotes());

        Station updated = stationRepository.save(station);

        // ✅ Audit log for update
        auditService.logAction(
                null,
                AuditAction.UPDATE,
                EntityType.STATION,
                updated.getStationId(),
                "Updated station: " + updated.getStationName(),
                null
        );

        // ✅ Notification for update
        notificationService.createNotification(
                "Station Updated",
                "Station \"" + updated.getStationName() + "\" has been updated.",
                NotificationSeverity.INFO,
                null
        );

        return updated;
    }

    // DELETE with validation
    @Override
    public void delete(Integer id) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Station not found with id: " + id)
                );

        boolean isUsed = borewellRepository.existsByStationStationId(id);

        if (isUsed) {
            throw new BadRequestException(
                    "Cannot delete station. It is assigned to existing borewells."
            );
        }

        stationRepository.delete(station);

        // ✅ Audit log for delete
        auditService.logAction(
                null,
                AuditAction.DELETE,
                EntityType.STATION,
                station.getStationId(),
                "Deleted station: " + station.getStationName(),
                null
        );

        // ✅ Notification for delete
        notificationService.createNotification(
                "Station Deleted",
                "Station \"" + station.getStationName() + "\" has been deleted.",
                NotificationSeverity.WARNING,
                null
        );
    }

    @Override
    public void deleteById(Integer id) {
        stationRepository.deleteById(id);
    }
}