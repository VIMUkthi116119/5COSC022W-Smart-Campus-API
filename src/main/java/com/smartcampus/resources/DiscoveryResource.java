package com.smartcampus.resources;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * Discovery resource providing HATEOAS links to the rest of the API.
 * This is the entry point for clients exploring the API capabilities.
 */
@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getDiscoveryLinks(@Context UriInfo uriInfo) {
        String baseUri = uriInfo.getBaseUri().toString();
        
        // HATEOAS Links implementation
        JsonObject links = Json.createObjectBuilder()
                .add("self", baseUri + "api/v1")
                .add("rooms", baseUri + "api/v1/rooms")
                .add("sensors", baseUri + "api/v1/sensors")
                .build();

        JsonObject response = Json.createObjectBuilder()
                .add("name", "Smart Campus Sensor API")
                .add("version", "1.0.0")
                .add("links", links)
                .build();

        return Response.ok(response).build();
    }
}
