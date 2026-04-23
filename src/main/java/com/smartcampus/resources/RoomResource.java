package com.smartcampus.resources;

import com.smartcampus.models.ErrorMessage;
import com.smartcampus.models.Room;
import com.smartcampus.storage.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST API Resource for managing Rooms.
 * Provides endpoints to list, retrieve, create, and delete rooms.
 */
@Path("/api/v1/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private DataStore dataStore = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        List<Room> rooms = new ArrayList<>(dataStore.getRooms().values());
        return Response.ok(rooms).build();
    }

    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") String id) {
        Room room = dataStore.getRoomById(id);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Room not found", 404))
                    .build();
        }
        
        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room room) {
        // Validation
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(422)
                    .entity(new ErrorMessage("Room name is required", 422))
                    .build();
        }

        room.setId("room-" + UUID.randomUUID().toString().substring(0, 8));
        dataStore.addRoom(room);

        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = dataStore.getRoomById(id);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Room not found", 404))
                    .build();
        }

        // Validation: Don't delete if there are sensors in the room
        if (!room.getSensorIds().isEmpty()) {
            return Response.status(422)
                    .entity(new ErrorMessage("Cannot delete room: it still has registered sensors", 422))
                    .build();
        }

        dataStore.deleteRoom(id);
        return Response.noContent().build();
    }
}
