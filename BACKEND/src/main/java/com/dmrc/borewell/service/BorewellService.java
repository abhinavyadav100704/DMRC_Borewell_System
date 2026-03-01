package com.dmrc.borewell.service;

import com.dmrc.borewell.entity.Borewell;

import java.util.List;
import java.util.Optional;

public interface BorewellService {

    Borewell save(Borewell borewell);

    List<Borewell> findAll();

    Optional<Borewell> findById(Integer id);

    Borewell update(Integer id, Borewell borewellDetails);

    void deleteById(Integer id);
}