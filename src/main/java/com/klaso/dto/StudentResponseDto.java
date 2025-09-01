package com.klaso.dto;

import com.klaso.entity.Student;

import java.time.LocalDate;

public class StudentResponseDto {
    public Long id;
    public String firstName;
    public String lastName;
    public LocalDate dateOfBirth;
    public String email;
    public String parentName;
    public String parentEmail;
    public String parentPhone;
    public String classroomName;
    public String classroomLevel;
    public String establishmentName;

    public StudentResponseDto(Student student) {
        this.id = student.getId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.dateOfBirth = student.getDateOfBirth();
        this.email = student.getEmail();
        this.parentName = student.getParentName();
        this.parentEmail = student.getParentEmail();
        this.parentPhone = student.getParentPhone();

        if (student.getClassroom() != null) {
            this.classroomName = student.getClassroom().getName();
            this.classroomLevel = student.getClassroom().getLevel();
            if (student.getClassroom().getEstablishment() != null) {
                this.establishmentName = student.getClassroom().getEstablishment().getName();
            }
        }
    }
}