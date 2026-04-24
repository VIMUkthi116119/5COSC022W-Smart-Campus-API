# 5COSC022W: Client-Server Architectures Coursework 2025/26

### Student Details

- **Name:** Vimukthi Ubeysekera
- **Student ID:** w2121327 / 20241227
- **Coursework Title:** Smart Campus Sensor & Room Management API
- **GitHub Repo:** https://github.com/VIMUkthi116119/5COSC022W-Smart-Campus-API.git

---

## 1. Project Overview

It is a high performance RESTful API that can be integrated into a **Smart Campus** ecosystem. This system has been created by me, as Lead Backend Architect, to efficiently handle infrastructure in the campus, particularly in the case of Room management and integration of IoT Sensor data, based upon the **JAX-RS** framework.

## 2. Technology Stack

- **Language:** Java 17
- **Framework:** JAX-RS (Jersey Implementation) - _Note: No Spring Boot used._
- **Build Tool:** Maven 3.6+
- **HTTP Server:** Embedded Grizzly Container
- **Data Storage:** In-memory (Singleton Pattern + `ConcurrentHashMap`)
- **Deployment:** Localhost (`http://localhost:8080/api/v1`)

## 3. How to Run the Project

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/VIMUkthi116119/5COSC022W-Smart-Campus-API.git
    ```
2.  **Open in IDE:**
    Open the project in **NetBeans IDE** (or any preferred Java IDE that supports Maven).
3.  **Run the Application:**
    Click **"Run Project"** in NetBeans.
    _(Alternatively, you can run it via terminal using: `mvn clean install` followed by `mvn exec:java`)_

    The API base URL will be: `http://localhost:8080/api/v1`

---

## 4. Coursework Report (Technical Analysis)

### 4.1 JAX-RS Lifecycle & Data Integrity (Section 1.1)

In JAX-RS, Resource classes are typically request-scoped. To maintain data integrity without a database, I implemented the **Singleton Pattern** for the `DataStore` class. Since multiple requests can access the data concurrently, I used **`ConcurrentHashMap`** and synchronized blocks to ensure **Thread-safety**, preventing race conditions during CRUD operations.

### 4.2 HATEOAS Justification (Section 1.2)

I implemented a Discovery Endpoint (`GET /api/v1`) using Hypermedia (HATEOAS). This makes the API **Self-documenting**, allowing clients to discover resources dynamically via links. This is superior to static documentation as it decouples the client from hardcoded URIs and improves system evolvability.

### 4.3 Error Handling: 422 vs 404 (Section 5.1)

The API distinguishes between resource presence and payload validity. I use **`422 Unprocessable Entity`** when the request syntax is correct but contains invalid logic (e.g., a Room ID that doesn't exist). This is more semantic and helpful to the client than a generic `404 Not Found`, which is reserved for cases where the endpoint URI itself is incorrect.

### 4.4 Security: Global Exception Mapping (Section 5.2)

Directly exposing Java stack traces is a significant **Cybersecurity risk**, as it reveals internal paths and library versions to attackers. I implemented a global `ApiExceptionMapper` and custom `ExceptionMappers` to intercept all errors. This ensures the client only receives a sanitized JSON `ErrorMessage`, protecting the server's internal logic.

### 4.5 Observability via Filters

Instead of duplicating logging logic, I used a `ContainerResponseFilter` (RequestLoggingFilter) to centrally log all request/response details, including HTTP status codes, ensuring a clean and maintainable codebase (DRY principle).

---

## 5. API Documentation (Endpoints)

| Method | Endpoint                        | Description                      | Status Code          |
| ------ | ------------------------------- | -------------------------------- | -------------------- |
| GET    | `/api/v1`                       | Discovery Endpoint (HATEOAS)     | 200 OK               |
| GET    | `/api/v1/rooms`                 | List all rooms                   | 200 OK               |
| POST   | `/api/v1/rooms`                 | Create a new room                | 201 Created          |
| DELETE | `/api/v1/rooms/{id}`            | Delete a room (if empty)         | 204 No Content / 409 |
| GET    | `/api/v1/sensors`               | List sensors (supports `?type=`) | 200 OK               |
| POST   | `/api/v1/sensors`               | Register a new sensor            | 201 Created          |
| POST   | `/api/v1/sensors/{id}/readings` | Add a reading to a sensor        | 201 Created          |

---

## 6. Testing & Validation

The API was rigorously tested using **Postman** to ensure it meets all functional and non-functional requirements. A comprehensive test suite of **12 scenarios** was executed:

1.  **API Discovery:** Verified that the root endpoint returns correct hypermedia links.
2.  **Room Creation:** Successfully added new rooms with valid JSON payloads (201 Created).
3.  **Sensor Registration:** Linked new sensors to existing rooms (201 Created).
4.  **Data Retrieval:** Verified `GET` requests for rooms and sensors.
5.  **Filtering:** Tested query parameters (e.g., `?type=Temperature`) to filter sensors.
6.  **Sub-resource Logic:** Added readings to specific sensors successfully.
7.  **Idempotency:** Verified that multiple `DELETE` calls for the same ID yield the same state.
8.  **Concurrency Test:** Verified that the `DataStore` handles rapid sequential requests without data corruption.
9.  **404 Not Found:** Handled invalid resource IDs using `LinkedResourceNotFoundException`.
10. **422 Validation:** Caught invalid room-sensor mappings during registration.
11. **409 Conflict:** Prevented deletion of rooms that still contain sensors (custom `RoomNotEmptyException`).
12. **403 Forbidden:** Prevented adding readings to sensors marked as 'Inactive' (custom `SensorUnavailableException`).

---

## 7. Sample cURL Commands

Use these commands in your terminal (CMD/PowerShell/Bash) to test the API directly:

```bash
# 1. API Discovery (HATEOAS Root)
curl -X GET http://localhost:8080/api/v1

# 2. Add a new Room
curl -X POST -H "Content-Type: application/json" -d "{\"id\":\"L4-01\", \"name\":\"Lab 1\", \"capacity\":30}" http://localhost:8080/api/v1/rooms

# 3. List all Rooms
curl -X GET http://localhost:8080/api/v1/rooms

# 4. Register a Sensor to a Room
curl -X POST -H "Content-Type: application/json" -d "{\"id\":\"SN-001\", \"type\":\"Temperature\", \"status\":\"ACTIVE\", \"roomId\":\"L4-01\"}" http://localhost:8080/api/v1/sensors

# 5. Filter Sensors by Type (Query Parameter)
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"

# 6. Add a Sensor Reading
curl -X POST -H "Content-Type: application/json" -d "{\"value\":24.5}" http://localhost:8080/api/v1/sensors/SN-001/readings

# 7. Test 409 Conflict: Delete a Room with active sensors
curl -X DELETE http://localhost:8080/api/v1/rooms/L4-01

# 8. Test 404 Not Found: Get a non-existent Room
curl -X GET http://localhost:8080/api/v1/rooms/INVALID-ID

# 9. Test 422 Unprocessable Entity: Add sensor to a fake room
curl -X POST -H "Content-Type: application/json" -d "{\"id\":\"SN-002\", \"type\":\"Motion\", \"status\":\"ACTIVE\", \"roomId\":\"FAKE-ROOM\"}" http://localhost:8080/api/v1/sensors

# 10. Add an Inactive Sensor
curl -X POST -H "Content-Type: application/json" -d "{\"id\":\"SN-003\", \"type\":\"Light\", \"status\":\"INACTIVE\", \"roomId\":\"L4-01\"}" http://localhost:8080/api/v1/sensors

# 11. Test 403 Forbidden: Add reading to the Inactive Sensor
curl -X POST -H "Content-Type: application/json" -d "{\"value\":100}" http://localhost:8080/api/v1/sensors/SN-003/readings

# 12. Test Idempotency: Update a room successfully
curl -X PUT -H "Content-Type: application/json" -d "{\"id\":\"L4-01\", \"name\":\"Updated Lab 1\", \"capacity\":40}" http://localhost:8080/api/v1/rooms/L4-01
```

## 8. Video Demonstration Link

**Link:** = https://www.youtube.com/watch?v=B51TIPi8RWA
