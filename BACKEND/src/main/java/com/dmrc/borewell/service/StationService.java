package com.dmrc.borewell.service;

import com.dmrc.borewell.entity.Station;

import java.util.List;
import java.util.Optional;

public interface StationService {

    // CREATE
    Station save(Station station);

    // READ
    List<Station> findAll();
    Optional<Station> findById(Integer id);

    // UPDATE
    Station update(Integer id, Station stationDetails);

    // DELETE
    void delete(Integer id);

    void deleteById(Integer id); // optional but kept for consistency
}