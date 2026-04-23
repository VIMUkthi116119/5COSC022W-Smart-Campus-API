# Smart Campus Sensor & Room Management API
**(5COSC022W - Client-Server Architectures Coursework)**

## 1. Project Overview
This project is a high-performance RESTful API designed for a **Smart Campus** ecosystem. As the Lead Backend Architect, I have developed this system to manage campus infrastructure efficiently, specifically focusing on Room management and IoT Sensor data integration.

*   **Scenario:** A centralized system to monitor campus resources (Rooms) and their associated environmental sensors (Temperature, CO2, etc.).
*   **Key Features:** 
    *   Full CRUD for Rooms and Sensors.
    *   Sub-resource mapping for adding and retrieving sensor readings.
    *   HATEOAS-driven API discovery for enhanced client navigability.
    *   Advanced custom exception handling with standardized JSON error responses.

## 2. Technology Stack
To ensure compliance with the technical constraints of the 5COSC022W module, the following stack was used:
*   **Core Framework:** JAX-RS (Jersey Implementation) - Chosen for its robust support for RESTful annotations.
*   **HTTP Server:** Embedded Grizzly Container - Provides a lightweight, high-performance environment without the overhead of a full application server.
*   **Build & Dependency Tool:** Apache Maven 3.6+
*   **Data Persistence:** Thread-safe In-memory Storage (Singleton Pattern + `ConcurrentHashMap`).
*   **Note:** No Spring Boot or SQL databases were used, adhering strictly to the coursework brief.

## 3. Setup and Build Instructions
Follow these steps to build and run the API locally:

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/VIMUkthi116119/5COSC022W-Smart-Campus-API.git
    ```
2.  **Build with Maven:**
    ```bash
    mvn clean package
    ```
3.  **Run the Application:**
    Navigate to the `target` folder and run the executable JAR:
    ```bash
    java -jar target/smart-campus-api-1.0.0.jar
    ```
    Alternatively, run directly via Maven:
    ```bash
    mvn exec:java
    ```
    The API will be available at: `http://localhost:8080/api/v1`

## 4. Conceptual Report (Critical Analysis)

### Part 1: Architecture & Discovery
*   **Lifecycle Analysis:** In JAX-RS, Resource classes are typically request-scoped, meaning a new instance is created for every HTTP request. To prevent data loss across requests, I implemented the **Singleton Pattern** for the `DataStore` class. This ensures that all resources interact with a single, synchronized instance of the data structures.
*   **HATEOAS Benefits:** By implementing a Discovery Endpoint (`/api/v1`), the API provides "Self-discovery." Clients receive hypermedia links in the response, allowing them to navigate the API dynamically without relying on external static documentation.

### Part 2: Room Management
*   **List Return Options:** When listing rooms, the API returns full object representations. While this increases initial bandwidth usage compared to returning only IDs, it significantly reduces "chattiness" by preventing the client from making multiple follow-up requests to fetch details for each room.
*   **DELETE Idempotency:** The `DELETE` operation in this API is idempotent. Regardless of whether a room is deleted once or multiple times, the final state of the server remains the same (the room does not exist). Subsequent calls return a consistent `404 Not Found` without side effects.

### Part 3: Sensor Operations
*   **Media Type Validation (@Consumes):** By using `@Consumes(MediaType.APPLICATION_JSON)`, the server automatically rejects requests with incorrect formats (like XML) with a `415 Unsupported Media Type` status, ensuring data integrity.
*   **QueryParam vs PathParam:** I utilized `QueryParam` for filtering (e.g., `GET /sensors?type=Temperature`). This keeps the base URL clean and allows for extensible filtering options without breaking the URI structure. `PathParam` is reserved for unique resource identification.

### Part 4: Sub-resources
*   **Sub-resource Mapping:** I implemented readings as a sub-resource of sensors (`/sensors/{id}/readings`). This hierarchical structure logically groups related data, making the API more intuitive for developers and reducing the complexity of the main resource classes.

### Part 5: Error Handling & Security
*   **422 vs 404 Logic:** The API uses `422 Unprocessable Entity` when a request is syntactically correct but contains logical errors (e.g., trying to add a sensor to a Room ID that doesn't exist). This is more descriptive than a generic `404` as it indicates the resource was reachable but the payload was invalid.
*   **Stack Trace Risks:** Direct exposure of Java stack traces in HTTP responses is a major security vulnerability. It provides attackers with information about the server's internal structure and library versions. I implemented a global `ApiExceptionMapper` to intercept all errors and return a sanitized JSON `ErrorMessage` instead.
*   **Filters for Observability:** Instead of duplicating logging code in every method, I implemented a `ContainerResponseFilter`. This handles all request/response logging centrally, improving code maintainability (DRY principle).

## 5. Sample cURL Commands
Test the API using the following commands:

```bash
# 1. API Discovery (HATEOAS)
curl -X GET http://localhost:8080/api/v1

# 2. Add a new Room
curl -X POST -H "Content-Type: application/json" -d "{\"id\":\"L4-01\", \"name\":\"Lab 1\", \"capacity\":30}" http://localhost:8080/api/v1/rooms

# 3. Add a Sensor to the Room
curl -X POST -H "Content-Type: application/json" -d "{\"id\":\"SN-001\", \"type\":\"Temperature\", \"unit\":\"Celsius\", \"roomId\":\"L4-01\"}" http://localhost:8080/api/v1/sensors

# 4. Filter Sensors by Type
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"

# 5. Add a Reading to a Sensor
curl -X POST -H "Content-Type: application/json" -d "{\"value\":24.5}" http://localhost:8080/api/v1/sensors/SN-001/readings
```

## 6. Video Demonstration Link
**Link:** [Attached in Blackboard Submission]
*(The video provides a full walkthrough of the code architecture and a live demonstration of all 12 test cases using Postman.)*

