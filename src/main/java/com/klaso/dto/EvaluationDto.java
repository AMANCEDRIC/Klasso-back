package com.klaso.dto;

import com.klaso.entity.Evaluation;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EvaluationDto {
    private Long id;
    private Long classroomId;
    private Long periodId;
    private String gradeType;
    private BigDecimal maxValue;
    private Integer coefficient;
    private LocalDate evaluationDate;
    private String description;

    public EvaluationDto() {
    }

    public EvaluationDto(Evaluation evaluation) {
        this.id = evaluation.getId();
        if (evaluation.getClassroom() != null) {
            this.classroomId = evaluation.getClassroom().getId();
        }
        if (evaluation.getPeriod() != null) {
            this.periodId = evaluation.getPeriod().getId();
        }
        this.gradeType = evaluation.getGradeType();
        this.maxValue = evaluation.getMaxValue();
        this.coefficient = evaluation.getCoefficient();
        this.evaluationDate = evaluation.getEvaluationDate();
        this.description = evaluation.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
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

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
