package com.klaso.dto;

import java.time.LocalDate;

public class StudentCreateDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private Long classroomId;

    // Constructeurs
    public StudentCreateDto() {
    }

    public StudentCreateDto(String firstName, String lastName, LocalDate dateOfBirth,
            String email, String parentName, String parentEmail,
            String parentPhone, Long classroomId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.parentName = parentName;
        this.parentEmail = parentEmail;
        this.parentPhone = parentPhone;
        this.classroomId = classroomId;
    }

    // Getters et Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }
}
