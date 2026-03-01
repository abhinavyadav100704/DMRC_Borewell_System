package com.dmrc.borewell.service.impl;

import com.dmrc.borewell.entity.Authority;
import com.dmrc.borewell.exception.BadRequestException;
import com.dmrc.borewell.repository.AuthorityRepository;
import com.dmrc.borewell.repository.BorewellRepository;
import com.dmrc.borewell.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private BorewellRepository borewellRepository;

    @Override
    public Authority save(Authority authority) {
        return authorityRepository.save(authority);
    }

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    @Override
    public Optional<Authority> findById(Integer id) {
        return authorityRepository.findById(id);
    }

    @Override
    public Authority update(Integer id, Authority authorityDetails) {
        return authorityRepository.findById(id)
                .map(existing -> {
                    existing.setName(authorityDetails.getName());
                    existing.setDesignation(authorityDetails.getDesignation());
                    existing.setContactNumber(authorityDetails.getContactNumber());
                    existing.setEmail(authorityDetails.getEmail());
                    return authorityRepository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public void deleteById(Integer id) {

        // 🔹 Check if authority is used in any borewell
        boolean isUsed = borewellRepository.existsByAuthorityAuthorityId(id);

        if (isUsed) {
            throw new BadRequestException(
                    "Cannot delete authority. It is assigned to existing borewells."
            );
        }

        authorityRepository.deleteById(id);
    }
}
