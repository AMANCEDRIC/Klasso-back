package com.klaso.dto;

import com.klaso.entity.Grade;

import java.math.BigDecimal;

public class GradeDto {

    private Long id;
    private BigDecimal value;
    private Boolean isAbsent;
    private String appreciation;
    private String status;

    private Long studentId;
    private String studentFirstName;
    private String studentLastName;

    private Long evaluationId;

    // Required by Jackson for deserialization of request bodies
    public GradeDto() {
    }

    public GradeDto(Grade grade) {
        this.id = grade.getId();
        this.value = grade.getValue();
        this.isAbsent = grade.getIsAbsent();
        this.appreciation = grade.getAppreciation();
        this.status = grade.getStatus();

        if (grade.getStudent() != null) {
            this.studentId = grade.getStudent().getId();
            this.studentFirstName = grade.getStudent().getFirstName();
            this.studentLastName = grade.getStudent().getLastName();
        }

        if (grade.getEvaluation() != null) {
            this.evaluationId = grade.getEvaluation().getId();
        }
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

    public Boolean getIsAbsent() {
        return isAbsent;
    }

    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    public String getAppreciation() {
        return appreciation;
    }

    public void setAppreciation(String appreciation) {
        this.appreciation = appreciation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }
}