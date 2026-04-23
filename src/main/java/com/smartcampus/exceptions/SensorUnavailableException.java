package com.smartcampus.exceptions;

import jakarta.ws.rs.WebApplicationException;

/**
 * Custom exception thrown when attempting to add a reading to a sensor 
 * that is marked as MAINTENANCE or OFFLINE.
 */
public class SensorUnavailableException extends WebApplicationException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
