package com.smartcampus.exceptions;

import jakarta.ws.rs.WebApplicationException;

/**
 * Custom exception thrown when attempting to delete a room that still has active sensors.
 */
public class RoomNotEmptyException extends WebApplicationException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
