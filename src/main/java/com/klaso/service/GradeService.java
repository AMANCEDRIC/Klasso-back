package com.klaso.service;

import com.klaso.entity.Grade;
import com.klaso.repository.GradeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class GradeService {

    @Inject
    GradeRepository gradeRepository;

    public List<Grade> getAll() {
        return gradeRepository.listAll();
    }

    public Grade findById(Long id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    @Transactional
    public Grade create(Grade grade) {
        grade.setCreatedAt(Instant.now());
        grade.setUpdatedAt(Instant.now());
        gradeRepository.persist(grade);
        return grade;
    }

    @Transactional
    public Grade update(Long id, Grade data) {
        Grade existing = gradeRepository.findById(id);
        if (existing == null) return null;

        existing.setValue(data.getValue());
        existing.setMaxValue(data.getMaxValue());
        existing.setCoefficient(data.getCoefficient());
        existing.setGradeType(data.getGradeType());
        existing.setSubject(data.getSubject());
        existing.setDescription(data.getDescription());
        existing.setGradeDate(data.getGradeDate());
        existing.setClassroom(data.getClassroom());
        existing.setUpdatedAt(Instant.now());

        return existing;
    }

    @Transactional
    public boolean delete(Long id) {
        return gradeRepository.deleteById(id);
    }
}