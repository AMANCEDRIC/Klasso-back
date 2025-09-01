package com.klaso.controller;


import com.klaso.dto.ApiResponse;
import com.klaso.entity.Establishment;
import com.klaso.service.EstablishmentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/establishments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstablishmentController {

    @Inject
    EstablishmentService establishmentService;

    @GET
    public Response getAll() {
        List<Establishment> list = establishmentService.getAll();
        return Response.ok(new ApiResponse<>(200, "Liste des établissements", list)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Establishment est = establishmentService.findById(id);
        if (est == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Établissement non trouvé", null)).build();
        }
        return Response.ok(new ApiResponse<>(200, "Établissement trouvé", est)).build();
    }

    @POST
    public Response create(Establishment est) {
        Establishment saved = establishmentService.create(est);
        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse<>(201, "Établissement créé avec succès", saved)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Establishment est) {
        Establishment updated = establishmentService.update(id, est);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(404, "Établissement non trouvé", null)).build();
        }
        return Response.ok(new ApiResponse<>(200, "Établissement mis à jour", updated)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = establishmentService.delete(id);
        if (deleted) {
            return Response.ok(new ApiResponse<>(200, "Établissement supprimé", null)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiResponse<>(404, "Établissement non trouvé", null)).build();
    }
}