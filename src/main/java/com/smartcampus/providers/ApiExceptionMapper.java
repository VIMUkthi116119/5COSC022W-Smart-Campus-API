package com.smartcampus.providers;

import com.smartcampus.models.ErrorMessage;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Global exception mapper to handle uncaught exceptions and WebApplicationExceptions.
 * Converts errors into a consistent JSON response using the ErrorMessage model.
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int statusCode = 500;
        String message = "Internal Server Error";

        if (exception instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) exception;
            statusCode = webEx.getResponse().getStatus();
            message = exception.getMessage();
        } else {
            // For general exceptions, we might want to log them here.
            exception.printStackTrace();
        }

        ErrorMessage errorMessage = new ErrorMessage(message, statusCode);

        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorMessage)
                .build();
    }
}
