package com.dmrc.borewell.service.impl;

import com.dmrc.borewell.entity.Station;
import com.dmrc.borewell.exception.BadRequestException;
import com.dmrc.borewell.repository.BorewellRepository;
import com.dmrc.borewell.repository.StationRepository;
import com.dmrc.borewell.service.StationService;
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

    @Override
    public Station createStation(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Override
    public Optional<Station> getStationById(Integer id) {
        return stationRepository.findById(id);
    }

    @Override
    public Station updateStation(Integer id, Station stationDetails) {
        return stationRepository.findById(id)
                .map(station -> {
                    station.setStationName(stationDetails.getStationName());
                    station.setLineId(stationDetails.getLineId());
                    station.setLocation(stationDetails.getLocation());
                    station.setPlatformCount(stationDetails.getPlatformCount());
                    station.setOpeningDate(stationDetails.getOpeningDate());
                    station.setStationType(stationDetails.getStationType());
                    station.setLastMaintenanceDate(stationDetails.getLastMaintenanceDate());
                    station.setMaintenanceNotes(stationDetails.getMaintenanceNotes());
                    return stationRepository.save(station);
                })
                .orElse(null);
    }

    @Override
    public void deleteStation(Integer id) {
        stationRepository.deleteById(id);
    }

    @Override
    public Station save(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    @Override
    public Optional<Station> findById(Integer id) {
        return stationRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {

        boolean isUsed = borewellRepository.existsByStationStationId(id);

        if (isUsed) {
            throw new BadRequestException(
                    "Cannot delete station. It is assigned to existing borewells."
            );
        }
        stationRepository.deleteById(id);

    }
}
