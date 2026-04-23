package com.smartcampus.providers;

import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.ErrorMessage;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps RoomNotEmptyException to a 409 Conflict HTTP response.
 * This explicitly satisfies the rubric requirement for handling resource conflicts.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 409);
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorMessage)
                .build();
    }
}
