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

@Path("/api/v1/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private DataStore dataStore = DataStore.getInstance();

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

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
