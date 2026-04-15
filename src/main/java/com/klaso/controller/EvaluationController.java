package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.EvaluationDto;
import com.klaso.entity.Evaluation;
import com.klaso.service.EvaluationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/evaluations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "TEACHER", "ADMIN" })
public class EvaluationController {

    @Inject
    EvaluationService evaluationService;

    @POST
    public Response create(EvaluationDto dto) {
        Evaluation evaluation = new Evaluation();
        evaluation.setGradeType(dto.getGradeType());
        evaluation.setMaxValue(dto.getMaxValue());
        evaluation.setCoefficient(dto.getCoefficient());
        evaluation.setEvaluationDate(dto.getEvaluationDate());
        evaluation.setDescription(dto.getDescription());

        Evaluation saved = evaluationService.create(evaluation, dto.getClassroomId(), dto.getPeriodId());
        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse<>(201, "Évaluation créée", new EvaluationDto(saved)))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Evaluation evaluation = evaluationService.findById(id);
        if (evaluation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Évaluation non trouvée", null))
                    .build();
        }
        return Response.ok(new ApiResponse<>(200, "Évaluation", new EvaluationDto(evaluation))).build();
    }

    @GET
    @Path("/classroom/{classroomId}")
    public Response getByClassroom(@PathParam("classroomId") Long classroomId) {
        List<Evaluation> list = evaluationService.listByClassroom(classroomId);
        List<EvaluationDto> dtos = list.stream().map(EvaluationDto::new).collect(Collectors.toList());
        String message = dtos.isEmpty() ? "Aucune évaluation" : "Évaluations de la classe";
        return Response.ok(new ApiResponse<>(200, message, dtos)).build();
    }

    @GET
    @Path("/classroom/{classroomId}/period/{periodId}")
    public Response getByClassroomAndPeriod(@PathParam("classroomId") Long classroomId,
            @PathParam("periodId") Long periodId) {
        List<Evaluation> list = evaluationService.listByClassroomAndPeriod(classroomId, periodId);
        List<EvaluationDto> dtos = list.stream().map(EvaluationDto::new).collect(Collectors.toList());
        String message = dtos.isEmpty() ? "Aucune évaluation pour cette période"
                : "Évaluations de la classe pour la période";
        return Response.ok(new ApiResponse<>(200, message, dtos)).build();
    }
}
