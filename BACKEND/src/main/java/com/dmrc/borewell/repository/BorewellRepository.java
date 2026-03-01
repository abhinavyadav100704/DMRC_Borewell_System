package com.dmrc.borewell.repository;

import com.dmrc.borewell.entity.Borewell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorewellRepository extends JpaRepository<Borewell, Integer> {
    boolean existsByAuthorityAuthorityId(Integer authorityId);
    boolean existsByStationStationId(Integer stationId);
}
