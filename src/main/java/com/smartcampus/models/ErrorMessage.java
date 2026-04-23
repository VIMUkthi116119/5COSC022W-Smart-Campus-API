package com.smartcampus.models;

/**
 * Represents a standardized error response for the API.
 * This ensures that all HTTP errors return a consistent JSON structure.
 */
public class ErrorMessage {
    private String error;
    private int statusCode;

    public ErrorMessage() {
    }

    public ErrorMessage(String error, int statusCode) {
        this.error = error;
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
