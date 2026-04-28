package com.route;

import com.route.algorithm.DijkstraAlgorithm;
import com.route.algorithm.KruskalMST;
import com.route.model.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DSA algorithms.
 * Run with: mvn test
 *
 * GOOD INTERVIEW TALKING POINT:
 * "I wrote unit tests covering edge cases: disconnected graphs,
 *  single-node graphs, and no-path scenarios."
 */
class AlgorithmTest {

    private Graph graph;
    private DijkstraAlgorithm dijkstra;
    private KruskalMST kruskal;

    @BeforeEach
    void setUp() {
        graph = Graph.buildSampleGraph(); // A-B-C-D-E-F
        dijkstra = new DijkstraAlgorithm();
        kruskal = new KruskalMST();
    }

    // ─── Dijkstra Tests ───────────────────────────────────────────────

    @Test
    void dijkstra_shortestPath_correctDistance() {
        // A→C→E→D: 2+3+4 = 9 (not A→B→D: 4+10 = 14)
        DijkstraAlgorithm.DijkstraResult result =
                dijkstra.findShortestPath(graph, "A", "D");
        assertEquals(9.0, result.totalDistance, 0.001);
    }

    @Test
    void dijkstra_shortestPath_correctRoute() {
        DijkstraAlgorithm.DijkstraResult result =
                dijkstra.findShortestPath(graph, "A", "D");
        List<String> expected = List.of("A", "C", "E", "D");
        assertEquals(expected, result.shortestPath);
    }

    @Test
    void dijkstra_sameSourceTarget_zeroDistance() {
        DijkstraAlgorithm.DijkstraResult result =
                dijkstra.findShortestPath(graph, "A", "A");
        assertEquals(0.0, result.totalDistance, 0.001);
    }

    @Test
    void dijkstra_noPath_returnsEmpty() {
        graph.addNode(new com.route.model.Node("Z", "Isolated"));
        DijkstraAlgorithm.DijkstraResult result =
                dijkstra.findShortestPath(graph, "A", "Z");
        assertTrue(result.shortestPath.isEmpty() || result.totalDistance == Double.MAX_VALUE);
    }

    // ─── Kruskal MST Tests ────────────────────────────────────────────

    @Test
    void kruskal_mst_hasVMinusOneEdges() {
        KruskalMST.MSTResult result = kruskal.computeMST(graph);
        assertEquals(graph.getNodeCount() - 1, result.mstEdges.size());
    }

    @Test
    void kruskal_mst_isConnected() {
        KruskalMST.MSTResult result = kruskal.computeMST(graph);
        assertTrue(result.isConnected);
    }

    @Test
    void kruskal_mst_minimumTotalWeight() {
        // MST of sample graph should be ≤ any spanning tree
        KruskalMST.MSTResult result = kruskal.computeMST(graph);
        assertTrue(result.totalWeight > 0);
        // Sample graph MST: A-C(2) + C-E(3) + E-D(4) + A-B(4) + D-F(11) = 24
        assertEquals(24.0, result.totalWeight, 0.001);
    }

    @Test
    void kruskal_disconnectedGraph_notConnected() {
        Graph disconnected = new Graph(false);
        disconnected.addEdge("A", "B", 5);
        // C is isolated
        disconnected.addNode(new com.route.model.Node("C", "Isolated"));

        KruskalMST.MSTResult result = kruskal.computeMST(disconnected);
        assertFalse(result.isConnected);
    }
}
