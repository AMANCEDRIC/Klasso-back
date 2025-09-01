package com.klaso.service;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.StudentCreateDto;
import com.klaso.dto.StudentResponseDto;
import com.klaso.entity.Classroom;
import com.klaso.entity.Student;
import com.klaso.repository.ClassroomRepository;
import com.klaso.repository.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class StudentService {

    @Inject
    StudentRepository studentRepository;

    @Inject
    ClassroomRepository classroomRepository;

    public ApiResponse<List<Student>> getAllStudents() {
        List<Student> students = studentRepository.listAll();
        return new ApiResponse<>(200, "Liste des élèves", students);
    }

//    public ApiResponse<List<StudentResponseDto>> getAllStudents() {
//        List<Student> students = studentRepository.listAll();
//        List<StudentResponseDto> studentDtos = students.stream()
//                .map(StudentResponseDto::new)
//                .toList();
//        return new ApiResponse<>(200, "Liste des élèves", studentDtos);
//    }

    public ApiResponse<List<Student>> getStudentsByClassroom(Long classroomId) {
        List<Student> students = studentRepository.findByClassroomId(classroomId);
        return new ApiResponse<>(200, "Élèves de la classe " + classroomId, students);
    }

    public ApiResponse<Student> getStudentById(Long id) {
        Student student = studentRepository.findById(id);
        if (student == null) {
            return new ApiResponse<>(404, "Élève non trouvé", null);
        }
        return new ApiResponse<>(200, "Détails de l'élève", student);
    }


    @Transactional
//    public ApiResponse<Student> createStudent(StudentCreateDto studentDto) {
//        // Récupérer la classe une seule fois
//        Classroom classroom = null;
//        if (studentDto.getClassroomId() != null) {
//            classroom = classroomRepository.findById(studentDto.getClassroomId());
//            if (classroom == null) {
//                return new ApiResponse<>(404, "Classe non trouvée", null);
//            }
//        }
//
//        // Créer l'entité Student
//        Student student = new Student();
//        student.setFirstName(studentDto.getFirstName());
//        student.setLastName(studentDto.getLastName());
//        student.setDateOfBirth(studentDto.getDateOfBirth());
//        student.setEmail(studentDto.getEmail());
//        student.setParentName(studentDto.getParentName());
//        student.setParentEmail(studentDto.getParentEmail());
//        student.setParentPhone(studentDto.getParentPhone());
//
//        // Associer la classe et la date d'inscription
//        student.setClassroom(classroom);
//        student.setEnrollmentDate(LocalDate.now());
//
//        student.setCreatedAt(Instant.now());
//        student.setUpdatedAt(Instant.now());
//        student.setIsActive(true);
//
//        studentRepository.persist(student);
//
//        return new ApiResponse<>(201, "Élève créé avec succès", student);
//    }

    public ApiResponse<StudentResponseDto> createStudent(StudentCreateDto studentDto) {
        Classroom classroom = null;
        if (studentDto.getClassroomId() != null) {
            classroom = classroomRepository.findById(studentDto.getClassroomId());
            if (classroom == null) {
                return new ApiResponse<>(404, "Classe non trouvée", null);
            }
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
        // Récupérer l'élève existant
        Student existing = studentRepository.findById(id);
        if (existing == null) {
            return new ApiResponse<>(404, "Élève non trouvé", null);
        }

        // Récupérer la classe si `classroomId` est fourni
        Classroom classroom = null;
        if (updatedDto.getClassroomId() != null) {
            classroom = classroomRepository.findById(updatedDto.getClassroomId());
            if (classroom == null) {
                return new ApiResponse<>(404, "Classe non trouvée", null);
            }
        }

        // Mettre à jour les informations de l'élève
        existing.setFirstName(updatedDto.getFirstName());
        existing.setLastName(updatedDto.getLastName());
        existing.setEmail(updatedDto.getEmail());
        existing.setDateOfBirth(updatedDto.getDateOfBirth());
        existing.setParentName(updatedDto.getParentName());
        existing.setParentEmail(updatedDto.getParentEmail());
        existing.setParentPhone(updatedDto.getParentPhone());
        existing.setClassroom(classroom); // Associer la classe
        existing.setUpdatedAt(Instant.now());

        // Mapper l'élève mis à jour vers le DTO de réponse
        StudentResponseDto studentDtoResponse = new StudentResponseDto(existing);
        return new ApiResponse<>(200, "Élève mis à jour", studentDtoResponse);
    }

    @Transactional
    public ApiResponse<Void> deleteStudent(Long id) {
        Student student = studentRepository.findById(id);
        if (student == null) {
            return new ApiResponse<>(404, "Élève non trouvé", null);
        }
        studentRepository.delete(student);
        return new ApiResponse<>(200, "Élève supprimé", null);
    }
}