package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.StudentCreateDto;
import com.klaso.dto.StudentResponseDto;
import com.klaso.entity.Student;
import com.klaso.service.StudentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentController {

    @Inject
    StudentService studentService;

    @GET
    public Response getAllStudents() {
        ApiResponse<List<StudentResponseDto>> response = studentService.getAllStudents();
        return Response.status(response.getStatus()).entity(response).build();
    }

    @GET
    @Path("/classroom/{classroomId}")
    public Response getStudentsByClassroom(@PathParam("classroomId") Long classroomId) {
        ApiResponse<List<StudentResponseDto>> response = studentService.getStudentsByClassroom(classroomId);
        return Response.status(response.getStatus()).entity(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getStudentById(@PathParam("id") Long id) {
        ApiResponse<StudentResponseDto> response = studentService.getStudentById(id);
        return Response.status(response.getStatus()).entity(response).build();
    }

    @POST
    public Response createStudent(StudentCreateDto studentDto) {
        ApiResponse<StudentResponseDto> response = studentService.createStudent(studentDto);
        return Response.status(response.getStatus()).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateStudent(@PathParam("id") Long id, StudentCreateDto student) {
        ApiResponse<StudentResponseDto> response = studentService.updateStudent(id, student);
        return Response.status(response.getStatus()).entity(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteStudent(@PathParam("id") Long id) {
        ApiResponse<Void> response = studentService.deleteStudent(id);
        return Response.status(response.getStatus()).entity(response).build();
    }
}