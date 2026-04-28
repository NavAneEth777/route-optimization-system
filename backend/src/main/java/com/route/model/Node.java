package com.route.model;

import java.util.Objects;

/**
 * Represents a location node in the route graph.
 * Each node has a unique ID and a display name (e.g. "City A", "Warehouse 3").
 *
 * DSA Note: This is the vertex in our Graph G = (V, E).
 * Space: O(1) per node.
 */
public class Node {
    private String id;
    private String name;
    private double latitude;   // optional: for map visualization
    private double longitude;

    public Node() {}

    public Node(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Node(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Node{id='" + id + "', name='" + name + "'}"; }
}
