package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.entity.Grade;
import com.klaso.repository.GradeRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Path("/api/grades/average")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"TEACHER", "ADMIN"})
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
        // Requête via evaluation.classroom.id (plus de lien direct grade → classroom)
        List<Grade> grades = gradeRepository.findByStudentIdAndClassroomId(studentId, classroomId);

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
                    continue;
                }
            }

            // Lire coefficient et maxValue depuis l'évaluation (source unique de vérité)
            int coef = 1;
            BigDecimal max = BigDecimal.valueOf(20);
            if (g.getEvaluation() != null) {
                coef = g.getEvaluation().getCoefficient() != null ? g.getEvaluation().getCoefficient() : 1;
                max = g.getEvaluation().getMaxValue() != null ? g.getEvaluation().getMaxValue() : BigDecimal.valueOf(20);
            }
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
