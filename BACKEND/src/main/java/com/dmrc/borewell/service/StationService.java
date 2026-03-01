package com.dmrc.borewell.service;

import com.dmrc.borewell.entity.Station;

import java.util.List;
import java.util.Optional;

public interface StationService {

    Station createStation(Station station);

    List<Station> getAllStations();

    Optional<Station> getStationById(Integer id);

    Station updateStation(Integer id, Station stationDetails);

    void deleteStation(Integer id);

    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findById(Integer id);

    void deleteById(Integer id);
}