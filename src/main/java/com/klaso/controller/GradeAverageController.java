package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.entity.Grade;
import com.klaso.repository.GradeRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Path("/api/grades/average")
@Produces(MediaType.APPLICATION_JSON)
public class GradeAverageController {

    @Inject
    GradeRepository gradeRepository;

    public static class AverageResponse {
        public String rule;
        public BigDecimal weightedAverage;
        public BigDecimal totalWeighted;
        public Integer totalCoefficient;
    }

    @GET
    @Path("/student/{studentId}/classroom/{classroomId}")
    public Response averageForStudentInClass(@PathParam("studentId") Long studentId,
                                             @PathParam("classroomId") Long classroomId,
                                             @QueryParam("rule") @DefaultValue("C") String rule) {
        List<Grade> grades = gradeRepository.list("student.id = ?1 and classroom.id = ?2", studentId, classroomId);

        BigDecimal totalWeighted = BigDecimal.ZERO;
        int totalCoefficient = 0;

        for (Grade g : grades) {
            String status = g.getStatus();
            boolean exclude = "ABSENT_JUSTIFIED".equals(status);
            BigDecimal value = g.getValue();
            if (value == null) {
                if ("ABSENT_UNJUSTIFIED".equals(status) || "NOT_SUBMITTED".equals(status)) {
                    value = BigDecimal.ZERO;
                } else if (exclude) {
                    continue;
                } else {
                    continue; // par défaut exclure nulls si non spécifié
                }
            }

            int coef = g.getCoefficient() != null ? g.getCoefficient() : 1;
            BigDecimal max = g.getMaxValue() != null ? g.getMaxValue() : BigDecimal.ONE;
            if (max.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal normalized = value.divide(max, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(20));
            totalWeighted = totalWeighted.add(normalized.multiply(BigDecimal.valueOf(coef)));
            totalCoefficient += coef;
        }

        AverageResponse resp = new AverageResponse();
        resp.rule = rule;
        resp.totalWeighted = totalWeighted.setScale(2, RoundingMode.HALF_UP);
        resp.totalCoefficient = totalCoefficient;
        resp.weightedAverage = totalCoefficient == 0
                ? BigDecimal.ZERO
                : totalWeighted.divide(BigDecimal.valueOf(totalCoefficient), 2, RoundingMode.HALF_UP);

        return Response.ok(new ApiResponse<>(200, "Moyenne calculée", resp)).build();
    }
}

