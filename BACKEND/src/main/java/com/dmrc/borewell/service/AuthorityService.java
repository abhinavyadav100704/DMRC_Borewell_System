package com.dmrc.borewell.service;

import com.dmrc.borewell.entity.Authority;

import java.util.List;
import java.util.Optional;

public interface AuthorityService {

    Authority save(Authority authority);

    List<Authority> findAll();

    Optional<Authority> findById(Integer id);

    Authority update(Integer id, Authority authorityDetails);

    void deleteById(Integer id);
}