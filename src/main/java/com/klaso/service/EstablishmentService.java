package com.klaso.service;


import com.klaso.entity.Establishment;
import com.klaso.repository.EstablishmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class EstablishmentService {

    @Inject
    EstablishmentRepository establishmentRepository;

    public List<Establishment> getAll() {
        return establishmentRepository.listAll();
    }

    @Transactional
    public Establishment create(Establishment establishment) {
        establishment.setCreatedAt(Instant.now());
        establishment.setUpdatedAt(Instant.now());
        establishmentRepository.persist(establishment);
        return establishment;
    }

    @Transactional
    public Establishment update(Long id, Establishment data) {
        Establishment existing = establishmentRepository.findById(id);
        if (existing == null) return null;

        existing.setName(data.getName());
        existing.setAddress(data.getAddress());
        existing.setCity(data.getCity());
        existing.setPostalCode(data.getPostalCode());
        existing.setCountry(data.getCountry());
        existing.setUpdatedAt(Instant.now());

        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        return establishmentRepository.deleteById(id);
    }

    public Establishment findById(Long id) {
        return establishmentRepository.findById(id);
    }
}