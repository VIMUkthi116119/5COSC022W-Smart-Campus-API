package com.smartcampus.resources;

import com.smartcampus.models.ErrorMessage;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.storage.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Resource class for managing Sensors.
 * Path: /api/v1/sensors
 * Handles CRUD operations, query filtering, and delegates reading-related operations to SensorReadingResource.
 */
@Path("/api/v1/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private DataStore dataStore = DataStore.getInstance();

    /**
     * Retrieves a list of all sensors.
     * Supports optional query parameter "type" (e.g., ?type=TEMPERATURE) to filter results.
     * Returns 200 OK with the filtered or full list of sensors.
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(dataStore.getSensors().values());

        // Filter by type if query param is given
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor s : sensorList) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filtered.add(s);
                }
            }
            return Response.ok(filtered).build();
        }

        return Response.ok(sensorList).build();
    }

    /**
     * Retrieves a specific sensor by its ID.
     * Returns 200 OK on success.
     * Returns 404 Not Found if the sensor ID does not exist.
     */
    @GET
    @Path("/{id}")
    public Response getSensorById(@PathParam("id") String id) {
        Sensor sensor = dataStore.getSensorById(id);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Sensor not found", 404))
                    .build();
        }

        return Response.ok(sensor).build();
    }

    /**
     * Registers a new Sensor and assigns it to a room.
     * Validates that both the sensor type and room ID are provided.
     * Returns 201 Created on success.
     * Returns 422 Unprocessable Entity if the provided roomId does not exist,
     * maintaining foreign key-like constraints in our in-memory data store.
     */
    @POST
    public Response createSensor(Sensor sensor) {
        // Validate required fields
        if (sensor.getType() == null || sensor.getType().isEmpty()) {
            return Response.status(422)
                    .entity(new ErrorMessage("Sensor type is required", 422))
                    .build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().isEmpty()) {
            return Response.status(422)
                    .entity(new ErrorMessage("Room ID is required", 422))
                    .build();
        }

        // Check the room actually exists
        Room room = dataStore.getRoomById(sensor.getRoomId());
        if (room == null) {
            return Response.status(422)
                    .entity(new ErrorMessage("The roomId provided does not exist", 422))
                    .build();
        }

        if (sensor.getStatus() == null || sensor.getStatus().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        sensor.setId("sensor-" + UUID.randomUUID().toString().substring(0, 8));
        dataStore.addSensor(sensor);

        // Link sensor to room
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    /**
     * Updates an existing sensor's properties (type, status, roomId).
     * Only provided fields are updated (Partial Update logic).
     * Returns 200 OK on success.
     * Returns 404 Not Found if the sensor does not exist.
     * Returns 422 Unprocessable Entity if attempting to move to a non-existent room.
     */
    @PUT
    @Path("/{id}")
    public Response updateSensor(@PathParam("id") String id, Sensor sensor) {
        Sensor existing = dataStore.getSensorById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Sensor not found", 404))
                    .build();
        }

        if (sensor.getType() != null && !sensor.getType().isEmpty()) {
            existing.setType(sensor.getType());
        }

        if (sensor.getStatus() != null && !sensor.getStatus().isEmpty()) {
            existing.setStatus(sensor.getStatus());
        }

        if (sensor.getRoomId() != null && !sensor.getRoomId().isEmpty()) {
            Room room = dataStore.getRoomById(sensor.getRoomId());
            if (room == null) {
                return Response.status(422)
                        .entity(new ErrorMessage("Room not found", 422))
                        .build();
            }
            existing.setRoomId(sensor.getRoomId());
        }

        dataStore.updateSensor(id, existing);
        return Response.ok(existing).build();
    }

    /**
     * Sub-resource locator for Sensor Readings.
     * Delegates all requests starting with /api/v1/sensors/{id}/readings to SensorReadingResource.
     * This keeps the controller logic modular and adheres to REST best practices.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
