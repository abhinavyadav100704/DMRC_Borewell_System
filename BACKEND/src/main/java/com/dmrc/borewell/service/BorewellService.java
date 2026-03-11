package com.dmrc.borewell.service;

import com.dmrc.borewell.entity.Borewell;

import java.util.List;
import java.util.Optional;

public interface BorewellService {

    List<Borewell> findAll();

    Optional<Borewell> findById(Integer id);

    Borewell update(Integer id, Borewell borewellDetails);

    Borewell create(Borewell borewell);

    void delete(Integer borewellId);
}