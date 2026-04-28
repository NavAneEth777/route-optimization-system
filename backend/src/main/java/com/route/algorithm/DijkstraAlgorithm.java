package com.route.algorithm;

import com.route.model.Edge;
import com.route.model.Graph;

import java.util.*;

/**
 * Dijkstra's Shortest Path Algorithm
 *
 * INTUITION:
 * Think of it as "greedy BFS with weights."
 * We always expand the node we can reach cheapest so far.
 * Like water flowing — it always takes the path of least resistance.
 *
 * ALGORITHM STEPS:
 * 1. dist[source] = 0, dist[all others] = ∞
 * 2. Put source into a MinHeap (priority queue)
 * 3. While heap is not empty:
 *    a. Pop node u with minimum distance
 *    b. For each neighbor v of u:
 *       - newDist = dist[u] + weight(u, v)
 *       - If newDist < dist[v]: update dist[v], push v to heap, record prev[v] = u
 * 4. Reconstruct path using prev[] array
 *
 * TIME COMPLEXITY:
 * - With binary heap (PriorityQueue): O((V + E) log V)
 * - With Fibonacci heap (advanced): O(E + V log V)
 * - We use Java's PriorityQueue → O((V + E) log V)
 *
 * SPACE COMPLEXITY: O(V) for dist[], prev[], visited sets
 *
 * INTERVIEW QUESTIONS:
 * Q: Why doesn't Dijkstra work with negative weights?
 * A: Once a node is popped from the heap as "finalized," we assume
 *    no shorter path exists. Negative edges can create shorter paths
 *    through already-finalized nodes. Use Bellman-Ford for negatives.
 *
 * Q: How is this different from BFS?
 * A: BFS finds shortest path by #hops (unweighted). Dijkstra finds
 *    shortest by total weight (weighted). BFS uses Queue (FIFO),
 *    Dijkstra uses PriorityQueue (min-weight first).
 */
public class DijkstraAlgorithm {

    /**
     * Result object containing shortest distance and reconstructed path.
     */
    public static class DijkstraResult {
        public final Map<String, Double> distances;    // shortest distance to each node
        public final Map<String, String> previous;     // prev node on shortest path
        public final List<String> shortestPath;        // path from source to target
        public final double totalDistance;             // total cost of path
        public final List<String> visitedOrder;        // nodes visited in order (for visualization)

        public DijkstraResult(Map<String, Double> distances,
                              Map<String, String> previous,
                              List<String> shortestPath,
                              double totalDistance,
                              List<String> visitedOrder) {
            this.distances = distances;
            this.previous = previous;
            this.shortestPath = shortestPath;
            this.totalDistance = totalDistance;
            this.visitedOrder = visitedOrder;
        }
    }

    /**
     * Run Dijkstra from source to target on a graph.
     *
     * @param graph    The weighted graph
     * @param sourceId Starting node ID
     * @param targetId Destination node ID
     * @return DijkstraResult with path and distances
     */
    public DijkstraResult findShortestPath(Graph graph, String sourceId, String targetId) {
        // ── 1. Initialize ──────────────────────────────────────────
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();
        List<String> visitedOrder = new ArrayList<>();

        // Set all distances to infinity
        for (String nodeId : graph.getAllNodeIds()) {
            dist.put(nodeId, Double.MAX_VALUE);
        }
        dist.put(sourceId, 0.0);

        // MinHeap: [distance, nodeId] — always pops minimum distance
        // PriorityQueue compares by first element of double[]
        PriorityQueue<double[]> minHeap = new PriorityQueue<>(
                Comparator.comparingDouble(a -> a[0])
        );
        // Store [distance, nodeId-hashCode] but we need string IDs
        // Better: use a custom class or encode differently
        // We'll use a Map for ID lookup
        PriorityQueue<NodeDist> pq = new PriorityQueue<>(
                Comparator.comparingDouble(nd -> nd.dist)
        );
        pq.offer(new NodeDist(sourceId, 0.0));

        // ── 2. Process ─────────────────────────────────────────────
        while (!pq.isEmpty()) {
            NodeDist current = pq.poll();
            String u = current.nodeId;

            // Skip if already finalized
            if (visited.contains(u)) continue;
            visited.add(u);
            visitedOrder.add(u);

            // Early exit if we reached target
            if (u.equals(targetId)) break;

            // ── 3. Relax edges ─────────────────────────────────────
            for (Edge edge : graph.getNeighbors(u)) {
                String v = edge.getDestination();
                if (visited.contains(v)) continue;

                double newDist = dist.get(u) + edge.getWeight();

                if (newDist < dist.get(v)) {
                    dist.put(v, newDist);
                    prev.put(v, u);
                    pq.offer(new NodeDist(v, newDist));
                    // Note: We don't remove old entry — "lazy deletion" pattern
                    // Old entries will be skipped by the visited check above
                }
            }
        }

        // ── 4. Reconstruct path ────────────────────────────────────
        List<String> path = reconstructPath(prev, sourceId, targetId);
        double totalDist = dist.getOrDefault(targetId, Double.MAX_VALUE);

        return new DijkstraResult(dist, prev, path, totalDist, visitedOrder);
    }

    /**
     * Walk backwards from target using prev[] map to reconstruct path.
     * Time: O(path length)
     */
    private List<String> reconstructPath(Map<String, String> prev,
                                          String source, String target) {
        List<String> path = new ArrayList<>();
        String current = target;

        // Walk back from target to source
        while (current != null) {
            path.add(0, current); // prepend
            current = prev.get(current);
        }

        // If path doesn't start with source, no path exists
        if (path.isEmpty() || !path.get(0).equals(source)) {
            return Collections.emptyList(); // no path found
        }
        return path;
    }

    // ─── Helper class ─────────────────────────────────────────────────
    private static class NodeDist {
        String nodeId;
        double dist;

        NodeDist(String nodeId, double dist) {
            this.nodeId = nodeId;
            this.dist = dist;
        }
    }
}
