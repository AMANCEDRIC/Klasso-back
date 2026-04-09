package com.klaso.dto;

import java.math.BigDecimal;

public class StudentAverageDto {
    public Long studentId;
    public String firstName;
    public String lastName;
    public BigDecimal annualAverage;
    public BigDecimal periodAverage;
    public Integer totalGrades;

    public StudentAverageDto() {}

    public StudentAverageDto(Long studentId, String firstName, String lastName, BigDecimal annualAverage, BigDecimal periodAverage, Integer totalGrades) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.annualAverage = annualAverage;
        this.periodAverage = periodAverage;
        this.totalGrades = totalGrades;
    }
}
