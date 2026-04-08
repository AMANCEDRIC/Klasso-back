package com.klaso.repository;

import com.klaso.entity.Period;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PeriodRepository implements PanacheRepository<Period> {

    public List<Period> findByEstablishmentId(Long establishmentId) {
        return list("establishment.id = ?1", establishmentId);
    }
}
