package com.route.model;

import java.util.*;

/**
 * Adjacency List Graph implementation.
 *
 * WHY ADJACENCY LIST over Adjacency Matrix?
 * - Matrix: O(V^2) space — wasteful for sparse graphs (real-world roads)
 * - List:   O(V + E) space — efficient for sparse graphs
 *
 * Real-world maps have ~4 edges per node on average (roads).
 * A city of 1000 nodes: Matrix needs 1M cells, List needs ~5000.
 *
 * Time complexities:
 * - Add node/edge: O(1)
 * - Get neighbors: O(degree)
 * - Check edge existence: O(degree)  [vs O(1) for matrix — trade-off]
 */
public class Graph {

    // V: map of node ID → Node object
    private Map<String, Node> nodes;

    // E: map of node ID → list of outgoing edges (adjacency list)
    private Map<String, List<Edge>> adjacencyList;

    private boolean directed;

    public Graph(boolean directed) {
        this.directed = directed;
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    // ─── Node Operations ────────────────────────────────────────────

    /**
     * Add a node to the graph.
     * O(1) average — HashMap insertion.
     */
    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public boolean hasNode(String nodeId) {
        return nodes.containsKey(nodeId);
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public Set<String> getAllNodeIds() {
        return nodes.keySet();
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    // ─── Edge Operations ────────────────────────────────────────────

    /**
     * Add a weighted edge. For undirected graphs, adds reverse edge too.
     * O(1) amortized — ArrayList append.
     */
    public void addEdge(String sourceId, String destId, double weight) {
        // Auto-create nodes if they don't exist
        if (!hasNode(sourceId)) addNode(new Node(sourceId, sourceId));
        if (!hasNode(destId))   addNode(new Node(destId, destId));

        adjacencyList.get(sourceId).add(new Edge(sourceId, destId, weight));

        if (!directed) {
            adjacencyList.get(destId).add(new Edge(destId, sourceId, weight));
        }
    }

    /**
     * Get all edges from a node (its neighbors).
     * O(1) — direct map lookup.
     */
    public List<Edge> getNeighbors(String nodeId) {
        return adjacencyList.getOrDefault(nodeId, Collections.emptyList());
    }

    /**
     * Get ALL edges in the graph (used by Kruskal's MST).
     * O(E) — iterate all adjacency lists.
     * For undirected: returns each edge once (avoids duplicates).
     */
    public List<Edge> getAllEdges() {
        List<Edge> all = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Map.Entry<String, List<Edge>> entry : adjacencyList.entrySet()) {
            for (Edge e : entry.getValue()) {
                // For undirected, avoid duplicate (A→B) and (B→A)
                String key = e.getSource().compareTo(e.getDestination()) < 0
                        ? e.getSource() + "-" + e.getDestination()
                        : e.getDestination() + "-" + e.getSource();
                if (directed || seen.add(key)) {
                    all.add(e);
                }
            }
        }
        return all;
    }

    // ─── Utility ────────────────────────────────────────────────────

    public int getNodeCount() { return nodes.size(); }
    public int getEdgeCount() { return getAllEdges().size(); }
    public boolean isDirected() { return directed; }

    /**
     * Build a sample graph for testing.
     * Topology: 5 cities, 7 roads.
     */
    public static Graph buildSampleGraph() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 4);
        g.addEdge("A", "C", 2);
        g.addEdge("B", "C", 5);
        g.addEdge("B", "D", 10);
        g.addEdge("C", "E", 3);
        g.addEdge("E", "D", 4);
        g.addEdge("D", "F", 11);

        // Name the nodes
        g.getNode("A").setName("Warehouse");
        g.getNode("B").setName("City B");
        g.getNode("C").setName("City C");
        g.getNode("D").setName("City D");
        g.getNode("E").setName("City E");
        g.getNode("F").setName("Delivery Hub");
        return g;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph [\n");
        for (Map.Entry<String, List<Edge>> entry : adjacencyList.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" → ").append(entry.getValue()).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
