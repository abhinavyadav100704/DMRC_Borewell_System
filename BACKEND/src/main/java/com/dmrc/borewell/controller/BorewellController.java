package com.dmrc.borewell.controller;

import com.dmrc.borewell.entity.Borewell;
import com.dmrc.borewell.entity.Station;
import com.dmrc.borewell.entity.Authority;
import com.dmrc.borewell.exception.ResourceNotFoundException;
import com.dmrc.borewell.exception.BadRequestException;
import com.dmrc.borewell.service.AuthorityService;
import com.dmrc.borewell.service.BorewellService;
import com.dmrc.borewell.service.StationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borewells")
public class BorewellController {

    @Autowired
    private BorewellService borewellService;

    @Autowired
    private StationService stationService;

    @Autowired
    private AuthorityService authorityService;

    // ✅ admin and user can create borewell
    @PostMapping
    public ResponseEntity<Borewell> createBorewell(@RequestBody Borewell borewell) {

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

        Borewell savedBorewell = borewellService.save(borewell);

        return ResponseEntity.ok(savedBorewell);
    }

    // ✅ Everyone can view all
    @GetMapping
    public List<Borewell> getAllBorewells() {
        return borewellService.findAll();
    }

    // ✅ Everyone can view by ID
    @GetMapping("/{id}")
    public ResponseEntity<Borewell> getBorewellById(@PathVariable Integer id) {

        Borewell borewell = borewellService.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Borewell not found with id: " + id)
                );

        return ResponseEntity.ok(borewell);
    }

    // ✅ Only ADMIN can update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Borewell> updateBorewell(@PathVariable Integer id,
                                                   @RequestBody Borewell borewellDetails) {

        Borewell existing = borewellService.findById(id)
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

        Borewell updated = borewellService.save(existing);

        return ResponseEntity.ok(updated);
    }

    // ✅ Only ADMIN can delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorewell(@PathVariable Integer id) {

        Borewell borewell = borewellService.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Borewell not found with id: " + id)
                );

        borewellService.deleteById(borewell.getBorewellId());

        return ResponseEntity.noContent().build();
    }
}
