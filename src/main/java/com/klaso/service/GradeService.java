package com.klaso.service;

import com.klaso.dto.GradeDto;
import com.klaso.entity.Classroom;
import com.klaso.entity.Grade;
import com.klaso.entity.Student;
import com.klaso.repository.ClassroomRepository;
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
    ClassroomRepository classroomRepository;

    public List<Grade> getAll() {
        return gradeRepository.listAll();
    }

    public Grade findById(Long id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> getGradesByClassroomId(Long classroomId) {
        return gradeRepository.findByClassroomId(classroomId);
    }

    @Transactional
    public Grade create(GradeDto dto) {
        Grade grade = new Grade();

        grade.setValue(dto.getValue());
        grade.setMaxValue(dto.getMaxValue());
        grade.setCoefficient(dto.getCoefficient());
        grade.setGradeType(dto.getGradeType());
        grade.setSubject(dto.getSubject());
        grade.setDescription(dto.getDescription());
        grade.setGradeDate(dto.getGradeDate());

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId());
            if (student == null) {
                throw new NotFoundException("Élève introuvable");
            }
            grade.setStudent(student);
        }

        if (dto.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(dto.getClassroomId());
            if (classroom == null) {
                throw new NotFoundException("Classe introuvable");
            }
            grade.setClassroom(classroom);
        }

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