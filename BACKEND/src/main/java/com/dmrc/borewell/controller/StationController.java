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
        return ResponseEntity.ok(
                stationService.save(station)
        );
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
    public ResponseEntity<Station> updateStation(
            @PathVariable Integer id,
            @RequestBody Station stationDetails) {

        return ResponseEntity.ok(
                stationService.update(id, stationDetails)
        );
    }

    // ✅ Only ADMIN can delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Integer id) {

        stationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}