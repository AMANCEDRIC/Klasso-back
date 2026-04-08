package com.klaso.repository;

import com.klaso.entity.Establishment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EstablishmentRepository implements PanacheRepository<Establishment> {

    public List<Establishment> findByOwner(Long accountId) {
        return list("owner.id", accountId);
    }

}