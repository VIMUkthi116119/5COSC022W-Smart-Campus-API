package com.smartcampus.storage;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton DataStore for in-memory persistence.
 * Implements the Singleton pattern to ensure a single source of truth across the entire application lifecycle.
 */
public class DataStore {
    private static DataStore instance;

    // Using ConcurrentHashMap to ensure thread-safety for in-memory storage, 
    // as JAX-RS resources are request-scoped by default and can be accessed concurrently by multiple threads.
    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private Map<String, SensorReading> readings = new ConcurrentHashMap<>();

    // Private constructor for Singleton
    private DataStore() {
        seedData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void seedData() {
        // Add sample room
        Room room1 = new Room("room-001", "Lecture Hall A", "Block 1");
        rooms.put(room1.getId(), room1);

        // Add sample sensor
        Sensor sensor1 = new Sensor("sensor-123", "TEMPERATURE", "ACTIVE", "room-001");
        sensors.put(sensor1.getId(), sensor1);
        room1.getSensorIds().add(sensor1.getId());

        // Add sample reading
        SensorReading sr = new SensorReading("read-abc", "sensor-123", 24.5, "2026-04-23T10:00:00Z");
        readings.put(sr.getId(), sr);
    }

    // --- Room Methods ---
    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoomById(String id) {
        return rooms.get(id);
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public void updateRoom(String id, Room room) {
        rooms.put(id, room);
    }

    public void deleteRoom(String id) {
        rooms.remove(id);
    }

    // --- Sensor Methods ---
    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Sensor getSensorById(String id) {
        return sensors.get(id);
    }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    public void updateSensor(String id, Sensor sensor) {
        sensors.put(id, sensor);
    }

    public void deleteSensor(String id) {
        sensors.remove(id);
    }

    // --- SensorReading Methods ---
    public Map<String, SensorReading> getReadings() {
        return readings;
    }

    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return readings.values().stream()
                .filter(r -> r.getSensorId().equals(sensorId))
                .collect(Collectors.toList());
    }

    public SensorReading getReadingById(String id) {
        return readings.get(id);
    }

    public void addReading(SensorReading reading) {
        readings.put(reading.getId(), reading);
    }
}
