package com.klaso.controller;

import com.klaso.dto.ApiResponse;
import com.klaso.entity.Period;
import com.klaso.service.PeriodService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/periods")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"TEACHER", "ADMIN"})
public class PeriodController {

    @Inject
    PeriodService periodService;

    @GET
    @Path("/establishment/{establishmentId}")
    public Response getByEstablishment(@PathParam("establishmentId") Long establishmentId) {
        List<Period> periods = periodService.findByEstablishmentId(establishmentId);
        return Response.ok(new ApiResponse<>(200, "Périodes de l'établissement", periods)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Period period = periodService.findById(id);
        return Response.ok(new ApiResponse<>(200, "Période trouvée", period)).build();
    }

    @POST
    @Path("/establishment/{establishmentId}")
    public Response create(@PathParam("establishmentId") Long establishmentId, Period period) {
        Period created = periodService.create(period, establishmentId);
        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse<>(201, "Période créée", created)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Period period) {
        Period updated = periodService.update(id, period);
        return Response.ok(new ApiResponse<>(200, "Période mise à jour", updated)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        periodService.delete(id);
        return Response.ok(new ApiResponse<>(200, "Période supprimée", null)).build();
    }
}
