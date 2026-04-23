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

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;
    private DataStore dataStore = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

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

    @POST
    public Response addReading(SensorReading reading) {
        // Validate sensor exists first
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Sensor not found", 404))
                    .build();
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
