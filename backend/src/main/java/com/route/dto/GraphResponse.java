package com.route.dto;

import com.route.model.Edge;
import com.route.model.Graph;
import com.route.model.Node;

import java.util.List;

/**
 * DTO for sending graph data to frontend.
 * Converts Graph internals to a JSON-serializable format.
 * Frontend (React + D3) expects: { nodes: [...], edges: [...] }
 */
public class GraphResponse {
    private List<Node> nodes;
    private List<Edge> edges;
    private int nodeCount;
    private int edgeCount;

    public GraphResponse(Graph graph) {
        this.nodes = List.copyOf(graph.getAllNodes());
        this.edges = graph.getAllEdges();
        this.nodeCount = graph.getNodeCount();
        this.edgeCount = graph.getEdgeCount();
    }

    public List<Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
    public int getNodeCount() { return nodeCount; }
    public int getEdgeCount() { return edgeCount; }
}
