package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class to bootstrap the Smart Campus API server.
 * 
 * Architecture Note: We are using Grizzly HTTP Server as an embedded server
 * instead of a heavy application server like Tomcat. This makes the application
 * lightweight, easy to run, and perfectly suited for microservice architectures.
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on. 
    // All API routes will be prefixed with this URL.
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Starts the Grizzly HTTP server and registers the JAX-RS application.
     * 
     * @return A running instance of the Grizzly HttpServer.
     */
    public static HttpServer startServer() {
        // ResourceConfig is the Jersey implementation of Application.
        // We instruct it to scan the 'com.smartcampus' package recursively.
        // This means any class annotated with @Path or @Provider in this package 
        // will automatically be discovered and registered (e.g., our Resources and ExceptionMappers).
        final ResourceConfig rc = new ResourceConfig().packages("com.smartcampus");

        // Creates and starts the embedded Grizzly server listening on BASE_URI, 
        // routing incoming requests to our registered Jersey resources.
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        // Initialize and start the server
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started at %s\nHit ENTER to stop it...", BASE_URI));
        
        // Wait for user input to gracefully shut down the server. 
        // This keeps the main thread alive while Grizzly worker threads handle incoming HTTP requests.
        System.in.read();
        server.shutdownNow();
    }
}
