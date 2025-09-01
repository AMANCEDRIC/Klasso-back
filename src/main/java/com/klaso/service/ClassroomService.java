package com.klaso.service;

import com.klaso.entity.Classroom;
import com.klaso.entity.Establishment;
import com.klaso.repository.ClassroomRepository;
import com.klaso.service.EstablishmentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class ClassroomService {

    @Inject
    ClassroomRepository classroomRepository;

    @Inject
    EstablishmentService establishmentService;

    public List<Classroom> getAll() {
        return classroomRepository.listAll();
    }

    public Classroom findById(Long id) {
        return classroomRepository.findById(id);
    }

    public List<Classroom> findByEstablishmentId(Long establishmentId) {
        return classroomRepository.findByEstablishmentId(establishmentId);
    }

    @Transactional
    public Classroom create(Classroom classroom, Long establishmentId) {
        if (establishmentId == null) {
            throw new WebApplicationException("L'ID de l'établissement est requis", Response.Status.BAD_REQUEST);
        }

        Establishment establishment = establishmentService.findById(establishmentId);
        if (establishment == null) {
            throw new WebApplicationException("Établissement non trouvé avec l'ID : " + establishmentId, Response.Status.NOT_FOUND);
        }

        classroom.setEstablishment(establishment);
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        classroomRepository.persist(classroom);
        return classroom;
    }

    @Transactional
    public Classroom update(Long id, Classroom data) {
        Classroom existing = classroomRepository.findById(id);
        if (existing == null) return null;

        existing.setName(data.getName());
        existing.setLevel(data.getLevel());
        existing.setSubject(data.getSubject());
        existing.setAcademicYear(data.getAcademicYear());
        existing.setUpdatedAt(Instant.now());

        // 👇 Force le chargement du champ LAZY avant la fin de la transaction
        if (existing.getEstablishment() != null) {
            existing.getEstablishment().getName(); // Force le chargement
        }

        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        return classroomRepository.deleteById(id);
    }
}