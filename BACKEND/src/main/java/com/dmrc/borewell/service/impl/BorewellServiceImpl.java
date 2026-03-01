package com.dmrc.borewell.service.impl;

import com.dmrc.borewell.entity.Borewell;
import com.dmrc.borewell.repository.BorewellRepository;
import com.dmrc.borewell.service.BorewellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BorewellServiceImpl implements BorewellService {

    @Autowired
    private BorewellRepository borewellRepository;

    @Override
    public Borewell save(Borewell borewell) {
        return borewellRepository.save(borewell);
    }

    @Override
    public List<Borewell> findAll() {
        return borewellRepository.findAll();
    }

    @Override
    public Optional<Borewell> findById(Integer id) {
        return borewellRepository.findById(id);
    }

    @Override
    public Borewell update(Integer id, Borewell borewellDetails) {
        return borewellRepository.findById(id)
                .map(existing -> {
                    existing.setBorewellNo(borewellDetails.getBorewellNo());
                    existing.setIsAvailable(borewellDetails.getIsAvailable());
                    existing.setDistanceM(borewellDetails.getDistanceM());
                    existing.setDiameter(borewellDetails.getDiameter());
                    existing.setDepth(borewellDetails.getDepth());
                    existing.setLocation(borewellDetails.getLocation());
                    existing.setApprovalDate(borewellDetails.getApprovalDate());
                    // If you have relationships:
                    if (borewellDetails.getStation() != null) {
                        existing.setStation(borewellDetails.getStation());
                    }
                    if (borewellDetails.getAuthority() != null) {
                        existing.setAuthority(borewellDetails.getAuthority());
                    }
                    return borewellRepository.save(existing);
                }).orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        borewellRepository.deleteById(id);
    }
}