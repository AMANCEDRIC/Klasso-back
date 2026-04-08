package com.klaso.service;

import com.klaso.entity.Classroom;
import com.klaso.entity.Evaluation;
import com.klaso.entity.Period;
import com.klaso.repository.ClassroomRepository;
import com.klaso.repository.EvaluationRepository;
import com.klaso.repository.PeriodRepository;
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

    @Inject
    PeriodRepository periodRepository;

    public List<Evaluation> listByClassroom(Long classroomId) {
        return evaluationRepository.findByClassroomId(classroomId);
    }

    public Evaluation findById(Long id) {
        return evaluationRepository.findById(id);
    }

    @Transactional
    public Evaluation create(Evaluation evaluation, Long classroomId, Long periodId) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if (classroom == null) {
            throw new NotFoundException("Classe introuvable");
        }
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
