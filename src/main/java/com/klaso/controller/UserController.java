package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.UserLoginDTO;
import com.klaso.dto.UserRegisterDTO;
import com.klaso.dto.UserResponseDTO;
import com.klaso.entity.User;
import com.klaso.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;

    @POST
    @Path("/register")
    public Response register(UserRegisterDTO dto) {
        User user = userService.register(dto);
        return Response.ok(new ApiResponse<>(201, "Utilisateur créé", new UserResponseDTO(user))).build();
    }

    @POST
    @Path("/login")
    public Response login(UserLoginDTO dto) {
        String token = userService.login(dto);
        return Response.ok(new ApiResponse<>(200, "Connexion réussie", token)).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("user")
    public Response getProfile(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        User user = userService.getUserByEmail(email);
        return Response.ok(new ApiResponse<>(200, "Infos utilisateur", new UserResponseDTO(user))).build();
    }
}