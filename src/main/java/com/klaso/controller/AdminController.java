package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.dto.UserResponseDTO;
import com.klaso.entity.Account;
import com.klaso.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AdminController {

    @Inject
    UserService userService;

    @GET
    @Path("/users")
    public Response getAllUsers() {
        List<Account> accounts = userService.getAllAccounts();
        List<UserResponseDTO> dtoList = accounts.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        
        return Response.ok(new ApiResponse<>(200, "Liste des utilisateurs", dtoList)).build();
    }

    @PUT
    @Path("/users/{id}/toggle-block")
    public Response toggleBlock(@PathParam("id") Long id) {
        userService.toggleBlock(id);
        return Response.ok(new ApiResponse<>(200, "Statut de l'utilisateur mis à jour", null)).build();
    }

    @DELETE
    @Path("/users/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.softDelete(id);
        return Response.ok(new ApiResponse<>(200, "Utilisateur supprimé (soft delete)", null)).build();
    }
}
