package com.klaso.service;

import com.klaso.entity.Classroom;
import com.klaso.entity.Evaluation;
import com.klaso.entity.Period;
import com.klaso.repository.EvaluationRepository;
import com.klaso.repository.PeriodRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EvaluationService {

    @Inject
    EvaluationRepository evaluationRepository;

    @Inject
    ClassroomService classroomService;

    @Inject
    PeriodRepository periodRepository;

    public List<Evaluation> listByClassroom(Long classroomId) {
        // Sécurisé par classroomService
        classroomService.findById(classroomId);
        return evaluationRepository.findByClassroomId(classroomId);
    }

    public List<Evaluation> listByClassroomAndPeriod(Long classroomId, Long periodId) {
        // Sécurisé par classroomService
        classroomService.findById(classroomId);
        return evaluationRepository.findByClassroomAndPeriod(classroomId, periodId);
    }

    public Evaluation findById(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id);
        if (evaluation == null) return null;
        
        // Sécurisé par classroomService via l'établissement
        classroomService.findById(evaluation.getClassroom().getId());
        
        return evaluation;
    }

    @Transactional
    public Evaluation create(Evaluation evaluation, Long classroomId, Long periodId) {
        // Sécurisé par classroomService
        Classroom classroom = classroomService.findById(classroomId);
        evaluation.setClassroom(classroom);

        if (periodId != null) {
            Period period = periodRepository.findById(periodId);
            if (period == null) {
                throw new NotFoundException("Période introuvable");
            }
            evaluation.setPeriod(period);
        }

        evaluation.setCreatedAt(Instant.now());
        evaluation.setUpdatedAt(Instant.now());
        evaluationRepository.persist(evaluation);
        return evaluation;
    }
}
