package com.dmrc.borewell.controller;

import com.dmrc.borewell.entity.Station;
import com.dmrc.borewell.exception.ResourceNotFoundException;
import com.dmrc.borewell.service.StationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    // ✅ admin and user can create station
    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody Station station) {
        Station saved = stationService.save(station);
        return ResponseEntity.ok(saved);
    }

    // ✅ Everyone can view all
    @GetMapping
    public List<Station> getAllStations() {
        return stationService.findAll();
    }

    // ✅ Everyone can view by ID
    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable Integer id) {

        Station station = stationService.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Station not found with id: " + id)
                );

        return ResponseEntity.ok(station);
    }

    // ✅ Only ADMIN can update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Station> updateStation(@RequestBody Station stationDetails,
                                                 @PathVariable Integer id) {

        Station station = stationService.findById(id)
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

        Station updatedStation = stationService.save(station);

        return ResponseEntity.ok(updatedStation);
    }

    // ✅ Only ADMIN can delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Integer id) {

        Station station = stationService.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Station not found with id: " + id)
                );

        stationService.deleteById(station.getStationId());

        return ResponseEntity.noContent().build();
    }
}
