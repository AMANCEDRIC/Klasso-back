package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.StudentAverageDto;
import com.klaso.entity.Grade;
import com.klaso.entity.Student;
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

    @Inject
    com.klaso.repository.StudentRepository studentRepository;

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
        List<Grade> grades = gradeRepository.findByStudentIdAndClassroomId(studentId, classroomId);
        return Response.ok(new ApiResponse<>(200, "Moyenne calculée", calculate(grades, rule))).build();
    }

    @GET
    @Path("/student/{studentId}/classroom/{classroomId}/period/{periodId}")
    public Response averageForStudentInClassByPeriod(@PathParam("studentId") Long studentId,
                                                     @PathParam("classroomId") Long classroomId,
                                                     @PathParam("periodId") Long periodId,
                                                     @QueryParam("rule") @DefaultValue("C") String rule) {
        List<Grade> grades = gradeRepository.findByStudentIdAndClassroomIdAndPeriodId(studentId, classroomId, periodId);
        return Response.ok(new ApiResponse<>(200, "Moyenne de période calculée", calculate(grades, rule))).build();
    }

    @GET
    @Path("/classroom/{classroomId}")
    public Response averageForClass(@PathParam("classroomId") Long classroomId,
                                    @QueryParam("rule") @DefaultValue("C") String rule) {
        List<Grade> grades = gradeRepository.findByClassroomId(classroomId);
        return Response.ok(new ApiResponse<>(200, "Moyenne de classe calculée", calculate(grades, rule))).build();
    }

    @GET
    @Path("/classroom/{classroomId}/period/{periodId}")
    public Response averageForClassByPeriod(@PathParam("classroomId") Long classroomId,
                                            @PathParam("periodId") Long periodId,
                                            @QueryParam("rule") @DefaultValue("C") String rule) {
        List<Grade> grades = gradeRepository.list("evaluation.classroom.id = ?1 and evaluation.period.id = ?2", classroomId, periodId);
        return Response.ok(new ApiResponse<>(200, "Moyenne de classe (période) calculée", calculate(grades, rule))).build();
    }

    @GET
    @Path("/classroom/{classroomId}/dashboard")
    public Response classDashboard(@PathParam("classroomId") Long classroomId,
                                   @QueryParam("periodId") Long periodId,
                                   @QueryParam("rule") @DefaultValue("C") String rule) {
        List<Student> students = studentRepository.findActiveStudentsByClassroom(classroomId);
        java.util.List<StudentAverageDto> dashboard = new java.util.ArrayList<>();

        for (Student s : students) {
            List<Grade> annualGrades = gradeRepository.findByStudentIdAndClassroomId(s.getId(), classroomId);
            AverageResponse annual = calculate(annualGrades, rule);

            AverageResponse period = null;
            if (periodId != null) {
                List<Grade> periodGrades = gradeRepository.findByStudentIdAndClassroomIdAndPeriodId(s.getId(), classroomId, periodId);
                period = calculate(periodGrades, rule);
            }

            dashboard.add(new StudentAverageDto(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                annual.weightedAverage,
                period != null ? period.weightedAverage : null,
                annualGrades.size()
            ));
        }

        return Response.ok(new ApiResponse<>(200, "Dashboard de classe", dashboard)).build();
    }

    private AverageResponse calculate(List<Grade> grades, String rule) {
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
        
        return resp;
    }
}
