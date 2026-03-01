package com.dmrc.borewell.repository;

import com.dmrc.borewell.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
}
