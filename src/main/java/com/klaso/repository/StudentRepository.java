package com.klaso.repository;

import com.klaso.entity.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class StudentRepository implements PanacheRepository<Student> {

    public List<Student> findByClassroomId(Long classroomId) {
        return list("classroom.id = ?1", classroomId);
    }

    public List<Student> findActiveStudents() {
        return list("isActive = true");
    }

    public List<Student> findActiveStudentsByClassroom(Long classroomId) {
        return list("classroom.id = ?1 and isActive = true", classroomId);
    }
}
