package com.smartcampus.exceptions;

import jakarta.ws.rs.WebApplicationException;

/**
 * Custom exception thrown when a resource relies on another resource that does not exist.
 * For example, POSTing a Sensor to a Room that isn't registered.
 */
public class LinkedResourceNotFoundException extends WebApplicationException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
