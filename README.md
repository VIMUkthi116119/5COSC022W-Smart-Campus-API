# 5COSC022W - Smart Campus API

## Project Overview
This project is a RESTful API designed for a Smart Campus environment, managing Rooms, Sensors, and Sensor Readings. It is built using Java and JAX-RS (Jersey), running on an embedded Grizzly HTTP Server.

## How to Run the Application
### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6+
- NetBeans IDE (optional, for easy execution)

### Running via Maven (Command Line)
1. Navigate to the root directory of the project.
2. Build the project using Maven:
   ```bash
   mvn clean package
   ```
3. Run the generated "Fat JAR":
   ```bash
   java -jar target/smart-campus-api-1.0.0.jar
   ```

### Running via NetBeans
1. Open the project in NetBeans.
2. Right-click the project `smart-campus-api` in the Projects window.
3. Select **Clean and Build**.
4. Right-click the project again and select **Run**.
5. The API will start at `http://localhost:8080/`

## API Endpoints Overview
The API supports HATEOAS. Accessing the root endpoint provides links to available resources.

- `GET /api/v1` : Discovery endpoint (HATEOAS).
- `GET /api/v1/rooms` : List all rooms.
- `POST /api/v1/rooms` : Create a new room.
- `GET /api/v1/sensors` : List all sensors (Supports query parameter `?type=`).
- `POST /api/v1/sensors` : Create a new sensor.
- `PUT /api/v1/sensors/{id}` : Update a sensor.
- `DELETE /api/v1/rooms/{id}` : Delete a room (validation prevents deletion if sensors exist).
- `POST /api/v1/sensors/{id}/readings` : Add a reading to a specific sensor.

## Lifecycle Analysis
The software development lifecycle for this REST API followed an iterative approach:
1. **Requirements Analysis**: Understanding the entities (Room, Sensor, Reading) and the constraints (e.g., a room cannot be deleted if it has active sensors).
2. **Design**: Choosing the JAX-RS framework (Jersey) with an embedded Grizzly server for a lightweight, microservice-like architecture. A Singleton `DataStore` pattern using `ConcurrentHashMap` was chosen for thread-safe in-memory data persistence.
3. **Implementation**: Developing the models, resources, and custom ExceptionMappers. Semantic HTTP status codes (201, 404, 422) were rigorously applied to ensure proper client communication.
4. **Testing**: Manual testing using Postman to verify CRUD operations, boundary conditions (e.g., missing fields), and custom error mapping.

## Security Risk Assessments
While this is a basic implementation, several security aspects were considered:
1. **Input Validation**: The API validates incoming JSON payloads. For instance, creating a room with an empty name or creating a sensor for a non-existent room yields a `422 Unprocessable Entity`.
2. **Exception Information Leakage**: The global `ApiExceptionMapper` intercepts all exceptions. Instead of returning raw HTML stack traces (which could expose server internals to attackers), it translates exceptions into a structured JSON `ErrorMessage`.
3. **Concurrency Issues**: Since JAX-RS creates a new resource instance per request, accessing a shared memory store can lead to race conditions. This risk is mitigated by using `ConcurrentHashMap` and synchronized blocks within the Singleton `DataStore`.
4. **Future Enhancements**: Currently, the API lacks Authentication/Authorization. A production environment would require implementing JWT or OAuth2 to restrict access to sensitive endpoints.
