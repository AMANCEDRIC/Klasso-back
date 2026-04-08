package com.klaso.service;

import com.klaso.entity.Classroom;
import com.klaso.entity.Establishment;
import com.klaso.repository.ClassroomRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClassroomService {

    @Inject
    ClassroomRepository classroomRepository;

    @Inject
    EstablishmentService establishmentService;

    public List<Classroom> getAll() {
        // Pour des raisons de sécurité, on filtre globalement
        // Un admin voit tout, un prof voit uniquement ses classes
        List<Classroom> all = classroomRepository.listAll();
        return all.stream()
                .filter(c -> {
                    try {
                        establishmentService.findById(c.getEstablishment().getId());
                        return true;
                    } catch (ForbiddenException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public Classroom findById(Long id) {
        Classroom classroom = classroomRepository.findById(id);
        if (classroom == null) return null;
        
        // Vérifie l'accès via l'établissement
        establishmentService.findById(classroom.getEstablishment().getId());
        
        return classroom;
    }

    public List<Classroom> findByEstablishmentId(Long establishmentId) {
        // La vérification de findById jette une exception si pas d'accès
        establishmentService.findById(establishmentId);
        return classroomRepository.findByEstablishmentId(establishmentId);
    }

    @Transactional
    public Classroom create(Classroom classroom, Long establishmentId) {
        if (establishmentId == null) {
            throw new WebApplicationException("L'ID de l'établissement est requis", Response.Status.BAD_REQUEST);
        }

        // findById sécurisé jette ForbiddenException si pas d'accès
        Establishment establishment = establishmentService.findById(establishmentId);
        
        classroom.setEstablishment(establishment);
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        classroomRepository.persist(classroom);
        return classroom;
    }

    @Transactional
    public Classroom update(Long id, Classroom data) {
        Classroom existing = findById(id); // findById est déjà sécurisé
        if (existing == null)
            return null;

        existing.setName(data.getName());
        existing.setLevel(data.getLevel());
        existing.setSubject(data.getSubject());
        existing.setAcademicYear(data.getAcademicYear());
        existing.setUpdatedAt(Instant.now());

        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        Classroom existing = findById(id); // findById est déjà sécurisé
        if (existing == null) return false;
        return classroomRepository.deleteById(id);
    }
}