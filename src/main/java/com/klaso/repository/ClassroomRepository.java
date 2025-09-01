package com.klaso.repository;

import com.klaso.entity.Classroom;
import io.quarkiverse.groovy.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ClassroomRepository implements PanacheRepository<Classroom> {

    public List<Classroom> findByEstablishmentId(Long establishmentId) {
        return list("establishment.id = ?1", establishmentId);
    }
}