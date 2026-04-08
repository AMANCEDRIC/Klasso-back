package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.ClassroomDto;
import com.klaso.dto.ClassroomResponseDto;
import com.klaso.entity.Classroom;
import com.klaso.service.ClassroomService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/classrooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"TEACHER", "ADMIN"})
public class ClassroomController {

    @Inject
    ClassroomService classroomService;

    @GET
    public Response getAll() {
        List<Classroom> list = classroomService.getAll();
        List<ClassroomResponseDto> dtoList = list.stream()
                .map(ClassroomResponseDto::fromEntity)
                .toList();

        return Response.ok(new ApiResponse<>(200, "Liste des classes", dtoList)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Classroom classroom = classroomService.findById(id);
        if (classroom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Classe non trouvée", null)).build();
        }

        ClassroomResponseDto dto = ClassroomResponseDto.fromEntity(classroom);
        return Response.ok(new ApiResponse<>(200, "Classe trouvée", dto)).build();
    }

    @POST
    public Response create(ClassroomDto classroomDto) {
        Classroom classroom = new Classroom();
        classroom.setName(classroomDto.name);
        classroom.setLevel(classroomDto.level);
        classroom.setSubject(classroomDto.subject);
        classroom.setAcademicYear(classroomDto.academicYear);

        Classroom created = classroomService.create(classroom, classroomDto.establishmentId);
        ClassroomResponseDto dto = ClassroomResponseDto.fromEntity(created);

        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse<>(201, "Classe créée", dto)).build();
    }

    @GET
    @Path("/establishment/{establishmentId}")
    public Response getByEstablishmentId(@PathParam("establishmentId") Long establishmentId) {
        List<Classroom> classrooms = classroomService.findByEstablishmentId(establishmentId);
        List<ClassroomResponseDto> dtoList = classrooms.stream()
                .map(ClassroomResponseDto::fromEntity)
                .toList();

        return Response.ok(new ApiResponse<>(200, "Classes de l'établissement " + establishmentId, dtoList)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Classroom classroom) {
        Classroom updated = classroomService.update(id, classroom);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Classe non trouvée", null)).build();
        }

        ClassroomResponseDto dto = ClassroomResponseDto.fromEntity(updated);
        return Response.ok(new ApiResponse<>(200, "Classe mise à jour", dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = classroomService.delete(id);
        if (deleted) {
            return Response.ok(new ApiResponse<>(200, "Classe supprimée", null)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiResponse<>(404, "Classe non trouvée", null)).build();
    }

}