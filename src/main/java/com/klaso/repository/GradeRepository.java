package com.klaso.repository;

import com.klaso.entity.Grade;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GradeRepository implements PanacheRepository<Grade> {

    public List<Grade> findByStudentId(Long studentId) {
        return list("student.id = ?1", studentId);
    }

    public List<Grade> findByEvaluationId(Long evaluationId) {
        return list("evaluation.id = ?1", evaluationId);
    }

    public List<Grade> findByClassroomId(Long classroomId) {
        return list("evaluation.classroom.id = ?1", classroomId);
    }

    public List<Grade> findByStudentIdAndClassroomId(Long studentId, Long classroomId) {
        return list("student.id = ?1 and evaluation.classroom.id = ?2", studentId, classroomId);
    }

    public List<Grade> findByStudentIdAndClassroomIdAndPeriodId(Long studentId, Long classroomId, Long periodId) {
        return list("student.id = ?1 and evaluation.classroom.id = ?2 and evaluation.period.id = ?3", studentId, classroomId, periodId);
    }
}