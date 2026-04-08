package com.klaso.service;

import com.klaso.dto.GradeDto;
import com.klaso.entity.Evaluation;
import com.klaso.entity.Grade;
import com.klaso.entity.Student;
import com.klaso.repository.EvaluationRepository;
import com.klaso.repository.GradeRepository;
import com.klaso.repository.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class GradeService {

    @Inject
    GradeRepository gradeRepository;

    @Inject
    StudentRepository studentRepository;

    @Inject
    EvaluationRepository evaluationRepository;

    public List<Grade> getAll() {
        return gradeRepository.listAll();
    }

    public Grade findById(Long id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> getGradesByEvaluationId(Long evaluationId) {
        return gradeRepository.findByEvaluationId(evaluationId);
    }

    @Transactional
    public Grade create(GradeDto dto) {
        Grade grade = new Grade();

        grade.setValue(dto.getValue());
        grade.setStatus(dto.getStatus());

        if (dto.getIsAbsent() != null) {
            grade.setIsAbsent(dto.getIsAbsent());
        }
        grade.setAppreciation(dto.getAppreciation());

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId());
            if (student == null) {
                throw new NotFoundException("Élève introuvable");
            }
            grade.setStudent(student);
        }

        if (dto.getEvaluationId() != null) {
            Evaluation evaluation = evaluationRepository.findById(dto.getEvaluationId());
            if (evaluation == null) {
                throw new NotFoundException("Évaluation introuvable");
            }
            grade.setEvaluation(evaluation);
        }

        grade.setCreatedAt(Instant.now());
        grade.setUpdatedAt(Instant.now());
        gradeRepository.persist(grade);
        return grade;
    }

    @Transactional
    public Grade update(Long id, GradeDto dto) {
        Grade existing = gradeRepository.findById(id);
        if (existing == null) return null;

        existing.setValue(dto.getValue());
        existing.setStatus(dto.getStatus());

        if (dto.getIsAbsent() != null) {
            existing.setIsAbsent(dto.getIsAbsent());
        }
        existing.setAppreciation(dto.getAppreciation());

        if (dto.getEvaluationId() != null) {
            Evaluation evaluation = evaluationRepository.findById(dto.getEvaluationId());
            if (evaluation != null) {
                existing.setEvaluation(evaluation);
            }
        }

        existing.setUpdatedAt(Instant.now());
        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        return gradeRepository.deleteById(id);
    }
}