package com.smartcampus.resources;

import com.smartcampus.models.ErrorMessage;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.storage.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Sub-resource for managing Sensor Readings.
 * Path: /api/v1/sensors/{id}/readings
 * This class is not annotated with @Path because instances are dynamically 
 * returned by the SensorResource sub-resource locator.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;
    private DataStore dataStore = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * Retrieves all readings for the parent sensor.
     * Returns 200 OK with a list of readings.
     * Returns 404 Not Found if the parent sensor ID is invalid.
     */
    @GET
    public Response getReadings() {
        // Validate sensor exists first
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Sensor not found", 404))
                    .build();
        }

        List<SensorReading> readings = dataStore.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }

    /**
     * Adds a new reading to the parent sensor.
     * Returns 201 Created on success.
     * Returns 404 Not Found if attempting to add a reading to a non-existent sensor.
     */
    @POST
    public Response addReading(SensorReading reading) {
        // Validate sensor exists first
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Sensor not found", 404))
                    .build();
        }

        // Validate sensor status
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) || "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new com.smartcampus.exceptions.SensorUnavailableException("Cannot add reading: Sensor is currently " + sensor.getStatus());
        }

        // Auto-generate ID and timestamp if not provided
        reading.setId("read-" + UUID.randomUUID().toString().substring(0, 8));
        reading.setSensorId(sensorId);
        
        if (reading.getTimestamp() == null || reading.getTimestamp().isEmpty()) {
            reading.setTimestamp(Instant.now().toString());
        }

        dataStore.addReading(reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
