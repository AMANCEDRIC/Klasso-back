package com.klaso.dto;

import com.klaso.entity.Grade;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GradeDto {

    private Long id;
    private BigDecimal value;
    private BigDecimal maxValue;
    private Integer coefficient;
    private String gradeType;
    private String subject;
    private String description;
    private LocalDate gradeDate;

    private Long studentId;
    private String studentFirstName;
    private String studentLastName;

    private Long classroomId;
    private String classroomName;

    private Long evaluationId;
    private String status;

    // Required by Jackson for deserialization of request bodies
    public GradeDto() {}

    public GradeDto(Grade grade) {
        this.id = grade.getId();
        this.value = grade.getValue();
        this.maxValue = grade.getMaxValue();
        this.coefficient = grade.getCoefficient();
        this.gradeType = grade.getGradeType();
        this.subject = grade.getSubject();
        this.description = grade.getDescription();
        this.gradeDate = grade.getGradeDate();

        if (grade.getStudent() != null) {
            this.studentId = grade.getStudent().getId();
            this.studentFirstName = grade.getStudent().getFirstName();
            this.studentLastName = grade.getStudent().getLastName();
        }

        if (grade.getClassroom() != null) {
            this.classroomId = grade.getClassroom().getId();
            this.classroomName = grade.getClassroom().getName();
        }

        if (grade.getEvaluation() != null) {
            this.evaluationId = grade.getEvaluation().getId();
        }

        this.status = grade.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Integer coefficient) {
        this.coefficient = coefficient;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(LocalDate gradeDate) {
        this.gradeDate = gradeDate;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }


}