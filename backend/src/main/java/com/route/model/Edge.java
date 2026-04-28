package com.route.model;

/**
 * Represents a weighted, directed edge between two nodes.
 *
 * DSA Note: This is the edge E in G = (V, E).
 * Weight typically represents distance, time, or cost.
 * For undirected graphs, we add edges in both directions.
 *
 * Space: O(1) per edge.
 * Total graph space: O(V + E) using adjacency list.
 */
public class Edge implements Comparable<Edge> {
    private String source;       // source node ID
    private String destination;  // destination node ID
    private double weight;       // distance / cost / time

    public Edge() {}

    public Edge(String source, String destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    /**
     * Comparable for use in Kruskal's MST (sort edges by weight).
     * Time: O(1) for each comparison.
     */
    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return "Edge{" + source + " -> " + destination + ", weight=" + weight + "}";
    }
}
