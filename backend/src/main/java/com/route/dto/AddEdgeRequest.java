package com.route.dto;

/**
 * DTO for adding an edge via REST API.
 * Separates API contract from internal Edge model.
 */
public class AddEdgeRequest {
    private String sourceId;
    private String destId;
    private double weight;

    public AddEdgeRequest() {}

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getDestId() { return destId; }
    public void setDestId(String destId) { this.destId = destId; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
