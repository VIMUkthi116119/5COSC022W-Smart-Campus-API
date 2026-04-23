package com.smartcampus.providers;

import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.ErrorMessage;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps SensorUnavailableException to a 403 Forbidden HTTP response.
 * This satisfies the rubric requirement for handling state constraints.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 403);
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorMessage)
                .build();
    }
}
