package com.klaso.exception;

import com.klaso.dto.ApiResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException ex) {
        int status = ex.getResponse().getStatus();
        String message = ex.getMessage();

        ApiResponse<?> response = new ApiResponse<>(status, message, null);

        return Response.status(status)
                .entity(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}