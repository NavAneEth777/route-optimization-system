package com.route.service;

import com.route.algorithm.DijkstraAlgorithm;
import com.route.algorithm.KruskalMST;
import com.route.model.Edge;
import com.route.model.Graph;
import com.route.model.Node;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Route Service — orchestrates graph operations.
 * This is the bridge between REST controllers and DSA algorithms.
 *
 * Architecture:
 * Controller → Service → Algorithm → Model
 * (HTTP)       (Logic)    (DSA)      (Data)
 */
@Service
public class RouteService {

    // In-memory graph store (replace with DB in production)
    private Graph graph = new Graph(false);

    private final DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
    private final KruskalMST kruskal = new KruskalMST();

    // ─── Graph Management ─────────────────────────────────────────────

    public void addNode(Node node) {
        graph.addNode(node);
    }

    public void addEdge(String sourceId, String destId, double weight) {
        graph.addEdge(sourceId, destId, weight);
    }

    public void resetGraph() {
        graph = new Graph(false);
    }

    public void loadSampleGraph() {
        graph = Graph.buildSampleGraph();
    }

    public Graph getGraph() {
        return graph;
    }

    // ─── Algorithm Execution ──────────────────────────────────────────

    /**
     * Find shortest path between two nodes using Dijkstra.
     */
    public DijkstraAlgorithm.DijkstraResult findShortestPath(String sourceId, String targetId) {
        if (!graph.hasNode(sourceId) || !graph.hasNode(targetId)) {
            throw new IllegalArgumentException(
                "Node not found: " + (!graph.hasNode(sourceId) ? sourceId : targetId)
            );
        }
        return dijkstra.findShortestPath(graph, sourceId, targetId);
    }

    /**
     * Compute Minimum Spanning Tree using Kruskal's algorithm.
     */
    public KruskalMST.MSTResult computeMST() {
        return kruskal.computeMST(graph);
    }

    /**
     * Compare naive (BFS) vs optimized (Dijkstra) routing.
     * Naive = treat all weights as 1 (BFS).
     * Optimized = actual shortest weighted path (Dijkstra).
     */
    public Map<String, Object> compareRoutes(String sourceId, String targetId) {
        Map<String, Object> result = new HashMap<>();

        // Optimized: Dijkstra weighted
        DijkstraAlgorithm.DijkstraResult dijkstraResult =
                dijkstra.findShortestPath(graph, sourceId, targetId);

        // Naive: unweighted BFS (treats every edge weight as 1)
        List<String> bfsPath = bfsUnweighted(sourceId, targetId);
        double naiveCost = bfsPath.isEmpty() ? -1 : calculatePathCost(bfsPath);

        result.put("dijkstraPath", dijkstraResult.shortestPath);
        result.put("dijkstraCost", dijkstraResult.totalDistance);
        result.put("naivePath", bfsPath);
        result.put("naiveCost", naiveCost);
        result.put("savings", naiveCost - dijkstraResult.totalDistance);

        return result;
    }

    // ─── Private Helpers ──────────────────────────────────────────────

    /**
     * BFS that ignores weights — finds path with fewest hops.
     * This is the "naive" approach for comparison.
     * Time: O(V + E)
     */
    private List<String> bfsUnweighted(String sourceId, String targetId) {
        Map<String, String> prev = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(sourceId);
        visited.add(sourceId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(targetId)) break;

            for (Edge edge : graph.getNeighbors(current)) {
                String neighbor = edge.getDestination();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    prev.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String node = targetId;
        while (node != null) {
            path.add(0, node);
            node = prev.get(node);
        }
        return path.size() > 0 && path.get(0).equals(sourceId) ? path : Collections.emptyList();
    }

    /**
     * Calculate actual weighted cost of a given path.
     */
    private double calculatePathCost(List<String> path) {
        double cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String u = path.get(i);
            String v = path.get(i + 1);
            for (Edge e : graph.getNeighbors(u)) {
                if (e.getDestination().equals(v)) {
                    cost += e.getWeight();
                    break;
                }
            }
        }
        return cost;
    }
}
