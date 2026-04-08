package com.klaso.service;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.StudentCreateDto;
import com.klaso.dto.StudentResponseDto;
import com.klaso.entity.Classroom;
import com.klaso.entity.Student;
import com.klaso.repository.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class StudentService {

    @Inject
    StudentRepository studentRepository;

    @Inject
    ClassroomService classroomService;

    public ApiResponse<List<Student>> getAllStudents() {
        // Un admin voit tout, un prof voit uniquement ses élèves
        List<Student> all = studentRepository.listAll();
        List<Student> filtered = all.stream()
                .filter(s -> {
                    try {
                        if (s.getClassroom() != null) {
                            classroomService.findById(s.getClassroom().getId());
                        }
                        return true;
                    } catch (ForbiddenException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        return new ApiResponse<>(200, "Liste des élèves", filtered);
    }

    public ApiResponse<List<Student>> getStudentsByClassroom(Long classroomId) {
        // classroomService.findById jette ForbiddenException si pas d'accès
        classroomService.findById(classroomId);
        List<Student> students = studentRepository.findByClassroomId(classroomId);
        return new ApiResponse<>(200, "Élèves de la classe " + classroomId, students);
    }

    public ApiResponse<Student> getStudentById(Long id) {
        Student student = studentRepository.findById(id);
        if (student == null) {
            return new ApiResponse<>(404, "Élève non trouvé", null);
        }
        
        // Vérifie l'accès via la classe
        if (student.getClassroom() != null) {
            classroomService.findById(student.getClassroom().getId());
        }
        
        return new ApiResponse<>(200, "Détails de l'élève", student);
    }

    @Transactional
    public ApiResponse<StudentResponseDto> createStudent(StudentCreateDto studentDto) {
        Classroom classroom = null;
        if (studentDto.getClassroomId() != null) {
            // Sécurisé par classroomService
            classroom = classroomService.findById(studentDto.getClassroomId());
        }

        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setDateOfBirth(studentDto.getDateOfBirth());
        student.setEmail(studentDto.getEmail());
        student.setParentName(studentDto.getParentName());
        student.setParentEmail(studentDto.getParentEmail());
        student.setParentPhone(studentDto.getParentPhone());
        student.setClassroom(classroom);
        student.setEnrollmentDate(LocalDate.now());
        student.setCreatedAt(Instant.now());
        student.setUpdatedAt(Instant.now());
        student.setIsActive(true);

        studentRepository.persist(student);

        StudentResponseDto studentDtoResponse = new StudentResponseDto(student);
        return new ApiResponse<>(201, "Élève créé avec succès", studentDtoResponse);
    }

    @Transactional
    public ApiResponse<StudentResponseDto> updateStudent(Long id, StudentCreateDto updatedDto) {
        // findById sécurisé
        ApiResponse<Student> response = getStudentById(id);
        if (response.getStatus() != 200) {
            return new ApiResponse<>(response.getStatus(), response.getMessage(), null);
        }
        Student existing = response.getData();

        Classroom classroom = null;
        if (updatedDto.getClassroomId() != null) {
            classroom = classroomService.findById(updatedDto.getClassroomId());
        }

        existing.setFirstName(updatedDto.getFirstName());
        existing.setLastName(updatedDto.getLastName());
        existing.setEmail(updatedDto.getEmail());
        existing.setDateOfBirth(updatedDto.getDateOfBirth());
        existing.setParentName(updatedDto.getParentName());
        existing.setParentEmail(updatedDto.getParentEmail());
        existing.setParentPhone(updatedDto.getParentPhone());
        existing.setClassroom(classroom);
        existing.setUpdatedAt(Instant.now());

        StudentResponseDto studentDtoResponse = new StudentResponseDto(existing);
        return new ApiResponse<>(200, "Élève mis à jour", studentDtoResponse);
    }

    @Transactional
    public ApiResponse<Void> deleteStudent(Long id) {
        ApiResponse<Student> response = getStudentById(id);
        if (response.getStatus() != 200) {
            return new ApiResponse<>(response.getStatus(), response.getMessage(), null);
        }
        studentRepository.delete(response.getData());
        return new ApiResponse<>(200, "Élève supprimé", null);
    }
}