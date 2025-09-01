package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.GradeDto;
import com.klaso.entity.Grade;
import com.klaso.service.GradeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/grades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GradeController {

    @Inject
    GradeService gradeService;

    @GET
    public Response getAll() {
        List<Grade> grades = gradeService.getAll();
        List<GradeDto> GradeDto = grades.stream().map(GradeDto::new).collect(Collectors.toList());
        return Response.ok(new ApiResponse<>(200,"Liste des notes",GradeDto)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Grade grade = gradeService.findById(id);
        if (grade == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Note non trouvée", null)).build();
        }
        return Response.ok(new ApiResponse<>(200, "Note trouvée", grade)).build();
    }

    @GET
    @Path("/student/{studentId}")
    public Response getByStudentId(@PathParam("studentId") Long studentId) {
        List<Grade> grades = gradeService.getGradesByStudentId(studentId);
        List<GradeDto> gradeDtos = grades.stream().map(GradeDto::new).collect(Collectors.toList());

        String message;
        if (!gradeDtos.isEmpty()) {
            GradeDto firstGrade = gradeDtos.get(0);
            String fullName = firstGrade.getStudentFirstName() + " " + firstGrade.getStudentLastName();
            message = "Notes de l'élève " + fullName;
        } else {
            message = "Aucune note trouvée pour cet élève";
        }

        return Response.ok(new ApiResponse<>(200, message, gradeDtos)).build();
    }

    @POST
    public Response create(Grade grade) {
        Grade saved = gradeService.create(grade);
        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse<>(201, "Note créée avec succès", saved)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Grade grade) {
        Grade updated = gradeService.update(id, grade);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Note non trouvée", null)).build();
        }
        return Response.ok(new ApiResponse<>(200, "Note mise à jour", updated)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = gradeService.delete(id);
        if (deleted) {
            return Response.ok(new ApiResponse<>(200, "Note supprimée", null)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiResponse<>(404, "Note non trouvée", null)).build();
    }
}