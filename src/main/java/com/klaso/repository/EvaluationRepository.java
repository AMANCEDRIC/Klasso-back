package com.klaso.repository;

import com.klaso.entity.Evaluation;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EvaluationRepository implements PanacheRepository<Evaluation> {

    public List<Evaluation> findByClassroomId(Long classroomId) {
        return list("classroom.id = ?1", classroomId);
    }

    public List<Evaluation> findByClassroomAndPeriod(Long classroomId, Long periodId) {
        return list("classroom.id = ?1 and period.id = ?2", classroomId, periodId);
    }
}

