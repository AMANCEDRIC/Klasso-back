package com.klaso.service;

import com.klaso.dto.GradeDto;
import com.klaso.entity.Evaluation;
import com.klaso.entity.Grade;
import com.klaso.entity.Student;
import com.klaso.repository.GradeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GradeService {

    @Inject
    GradeRepository gradeRepository;

    @Inject
    StudentService studentService;

    @Inject
    EvaluationService evaluationService;

    public List<Grade> getAll() {
        // Filtrage global par accès étudiant/évaluation
        List<Grade> all = gradeRepository.listAll();
        return all.stream()
                .filter(g -> {
                    try {
                        if (g.getStudent() != null) {
                            studentService.getStudentById(g.getStudent().getId());
                        }
                        return true;
                    } catch (ForbiddenException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public Grade findById(Long id) {
        Grade grade = gradeRepository.findById(id);
        if (grade == null) return null;
        
        // Sécurisé via studentService
        if (grade.getStudent() != null) {
            studentService.getStudentById(grade.getStudent().getId());
        }
        
        return grade;
    }

    public List<Grade> getGradesByStudentId(Long studentId) {
        // studentService jette Forbidden si pas d'accès
        studentService.getStudentById(studentId);
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> getGradesByEvaluationId(Long evaluationId) {
        // evaluationService jette Forbidden si pas d'accès
        evaluationService.findById(evaluationId);
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
            // studentService sécurisé
            studentService.getStudentById(dto.getStudentId());
            Student student = studentService.studentRepository.findById(dto.getStudentId());
            grade.setStudent(student);
        }

        if (dto.getEvaluationId() != null) {
            // evaluationService sécurisé
            Evaluation evaluation = evaluationService.findById(dto.getEvaluationId());
            grade.setEvaluation(evaluation);
        }

        grade.setCreatedAt(Instant.now());
        grade.setUpdatedAt(Instant.now());
        gradeRepository.persist(grade);
        return grade;
    }

    @Transactional
    public Grade update(Long id, GradeDto dto) {
        Grade existing = findById(id); // déjà sécurisé
        if (existing == null) return null;

        existing.setValue(dto.getValue());
        existing.setStatus(dto.getStatus());

        if (dto.getIsAbsent() != null) {
            existing.setIsAbsent(dto.getIsAbsent());
        }
        existing.setAppreciation(dto.getAppreciation());

        if (dto.getEvaluationId() != null) {
            Evaluation evaluation = evaluationService.findById(dto.getEvaluationId());
            if (evaluation != null) {
                existing.setEvaluation(evaluation);
            }
        }

        existing.setUpdatedAt(Instant.now());
        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        Grade existing = findById(id); // déjà sécurisé
        if (existing == null) return false;
        return gradeRepository.deleteById(id);
    }
}