package com.klaso.service;

import com.klaso.entity.Classroom;
import com.klaso.entity.Evaluation;
import com.klaso.repository.ClassroomRepository;
import com.klaso.repository.EvaluationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class EvaluationService {

    @Inject
    EvaluationRepository evaluationRepository;

    @Inject
    ClassroomRepository classroomRepository;

    public List<Evaluation> listByClassroom(Long classroomId) {
        return evaluationRepository.findByClassroomId(classroomId);
    }

    public Evaluation findById(Long id) {
        return evaluationRepository.findById(id);
    }

    @Transactional
    public Evaluation create(Evaluation evaluation, Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if (classroom == null) {
            throw new NotFoundException("Classe introuvable");
        }
        evaluation.setClassroom(classroom);
        evaluation.setCreatedAt(Instant.now());
        evaluation.setUpdatedAt(Instant.now());
        evaluationRepository.persist(evaluation);
        return evaluation;
    }
}

