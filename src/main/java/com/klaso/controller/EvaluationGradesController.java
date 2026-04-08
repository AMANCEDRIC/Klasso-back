package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.GradeDto;
import com.klaso.entity.Evaluation;
import com.klaso.entity.Grade;
import com.klaso.entity.Student;
import com.klaso.repository.EvaluationRepository;
import com.klaso.repository.GradeRepository;
import com.klaso.repository.StudentRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/api/evaluations/{evaluationId}/grades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluationGradesController {

    public static class BulkGradesRequest {
        public static class Item {
            public Long studentId;
            public java.math.BigDecimal value;
            public String status; // PRESENT, ABSENT_JUSTIFIED, ABSENT_UNJUSTIFIED, NOT_SUBMITTED
            public Boolean isAbsent;
            public String appreciation;
        }
        public List<Item> grades;
    }

    @Inject
    EvaluationRepository evaluationRepository;

    @Inject
    GradeRepository gradeRepository;

    @Inject
    StudentRepository studentRepository;

    @GET
    public Response list(@PathParam("evaluationId") Long evaluationId) {
        List<Grade> list = gradeRepository.list("evaluation.id = ?1", evaluationId);
        List<GradeDto> dtos = list.stream().map(GradeDto::new).collect(Collectors.toList());
        return Response.ok(new ApiResponse<>(200, "Notes de l'évaluation", dtos)).build();
    }

    @POST
    @Transactional
    public Response bulkUpsert(@PathParam("evaluationId") Long evaluationId, BulkGradesRequest request) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId);
        if (evaluation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Évaluation non trouvée", null)).build();
        }

        Map<Long, Grade> existingByStudent = gradeRepository.list("evaluation.id = ?1", evaluationId)
                .stream().collect(Collectors.toMap(g -> g.getStudent().getId(), g -> g));

        for (BulkGradesRequest.Item item : request.grades) {
            Student student = studentRepository.findById(item.studentId);
            if (student == null) {
                continue; // ignorer les ids invalides
            }
            Grade grade = existingByStudent.get(item.studentId);
            if (grade == null) {
                grade = new Grade();
                grade.setEvaluation(evaluation);
                grade.setStudent(student);
                grade.setCreatedAt(Instant.now());
            }
            grade.setValue(item.value);
            grade.setStatus(item.status);
            if (item.isAbsent != null) {
                grade.setIsAbsent(item.isAbsent);
            }
            grade.setAppreciation(item.appreciation);
            grade.setUpdatedAt(Instant.now());

            if (grade.getId() == null) {
                gradeRepository.persist(grade);
            }
        }

        List<Grade> result = gradeRepository.list("evaluation.id = ?1", evaluationId);
        List<GradeDto> dtos = result.stream().map(GradeDto::new).collect(Collectors.toList());
        return Response.ok(new ApiResponse<>(200, "Notes enregistrées", dtos)).build();
    }
}
