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

    public List<Grade> findByClassroomId(Long classroomId) {
        return list("classroom.id = ?1", classroomId);
    }
}