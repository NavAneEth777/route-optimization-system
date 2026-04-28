package com.route.algorithm;

import com.route.model.Edge;
import com.route.model.Graph;

import java.util.*;

/**
 * Kruskal's Minimum Spanning Tree Algorithm with Union-Find (Disjoint Set Union)
 *
 * INTUITION:
 * You're building a cheapest road network connecting ALL cities.
 * Sort all roads by cost. Greedily add cheapest road that doesn't create a cycle.
 *
 * WHY MST MATTERS FOR ROUTE OPTIMIZATION?
 * - Delivery company: cheapest set of roads to connect all warehouses
 * - Network design: minimum cable to connect all buildings
 * - ATM networks: minimum fiber to connect all ATMs
 *
 * ALGORITHM STEPS:
 * 1. Sort ALL edges by weight (ascending)
 * 2. For each edge (u, v) in sorted order:
 *    - If u and v are in DIFFERENT components (find(u) ≠ find(v)):
 *      → Add edge to MST
 *      → Union(u, v) — merge their components
 *    - If same component → skip (would create cycle)
 * 3. Stop when MST has V-1 edges
 *
 * UNION-FIND (Disjoint Set Union):
 * - The magic data structure that checks "are u and v connected?"
 * - find(x): which group does x belong to? (with path compression)
 * - union(x, y): merge groups of x and y (with rank)
 *
 * TIME COMPLEXITY:
 * - Sort edges: O(E log E)
 * - Union-Find operations: O(E α(V)) ≈ O(E) [α is inverse Ackermann, ~constant]
 * - Total: O(E log E)
 *
 * SPACE COMPLEXITY: O(V) for Union-Find arrays
 *
 * vs PRIM'S ALGORITHM:
 * - Kruskal: edge-centric, better for sparse graphs (E << V^2)
 * - Prim: vertex-centric, better for dense graphs
 * - Both produce valid MSTs, may differ in structure
 *
 * INTERVIEW QUESTIONS:
 * Q: Why use Union-Find instead of DFS to detect cycles?
 * A: DFS cycle detection is O(V+E) per check. Union-Find is O(α(V)) ≈ O(1).
 *    Over E edges: DFS = O(E(V+E)), Union-Find = O(E α(V)).
 *
 * Q: What is path compression in Union-Find?
 * A: When doing find(x), flatten the tree by pointing all nodes directly
 *    to root. Makes future finds O(1) amortized.
 */
public class KruskalMST {

    // ─── Union-Find (Disjoint Set Union) ────────────────────────────────

    /**
     * Maps node ID strings to integer indices for Union-Find arrays.
     * Union-Find works on integers, not strings.
     */
    private Map<String, Integer> nodeIndex;
    private int[] parent;
    private int[] rank;

    private void initUnionFind(Collection<String> nodeIds) {
        nodeIndex = new HashMap<>();
        int i = 0;
        for (String id : nodeIds) {
            nodeIndex.put(id, i++);
        }
        parent = new int[nodeIds.size()];
        rank   = new int[nodeIds.size()];

        // Initially, each node is its own parent (separate components)
        for (int j = 0; j < parent.length; j++) {
            parent[j] = j;
            rank[j]   = 0;
        }
    }

    /**
     * FIND: Find root of component containing x.
     * PATH COMPRESSION: On the way back up, point every node directly to root.
     * This flattens the tree, making future finds nearly O(1).
     *
     * Example: find(E) in chain A←B←C←D←E
     * After: E→A, D→A, C→A (all point directly to root)
     */
    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression (recursive)
        }
        return parent[x];
    }

    /**
     * UNION: Merge components of x and y.
     * UNION BY RANK: Attach smaller tree under larger tree.
     * This keeps tree height ≤ log(n).
     *
     * @return true if merged (were in different components), false if same
     */
    private boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) return false; // already connected → would form cycle

        // Union by rank: smaller rank tree goes under larger rank tree
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            // Equal rank: arbitrary choice, increment winner's rank
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }

    // ─── MST Result ──────────────────────────────────────────────────────

    public static class MSTResult {
        public final List<Edge> mstEdges;      // edges in the MST
        public final double totalWeight;        // sum of all MST edge weights
        public final boolean isConnected;       // false if graph is disconnected

        public MSTResult(List<Edge> mstEdges, double totalWeight, boolean isConnected) {
            this.mstEdges = mstEdges;
            this.totalWeight = totalWeight;
            this.isConnected = isConnected;
        }
    }

    // ─── Main Kruskal Algorithm ──────────────────────────────────────────

    /**
     * Run Kruskal's MST on the given graph.
     *
     * @param graph An undirected, weighted graph
     * @return MSTResult with MST edges and total weight
     */
    public MSTResult computeMST(Graph graph) {
        int V = graph.getNodeCount();

        // Step 1: Initialize Union-Find
        initUnionFind(graph.getAllNodeIds());

        // Step 2: Get all edges and sort by weight (Kruskal's greedy step)
        List<Edge> allEdges = graph.getAllEdges();
        Collections.sort(allEdges); // Edge implements Comparable by weight

        // Step 3: Greedily add edges that don't form cycles
        List<Edge> mstEdges = new ArrayList<>();
        double totalWeight = 0;

        for (Edge edge : allEdges) {
            // Stop early: MST has exactly V-1 edges
            if (mstEdges.size() == V - 1) break;

            int u = nodeIndex.get(edge.getSource());
            int v = nodeIndex.get(edge.getDestination());

            // union() returns true if they were in different components
            // (i.e., adding this edge doesn't create a cycle)
            if (union(u, v)) {
                mstEdges.add(edge);
                totalWeight += edge.getWeight();
            }
            // else: skip — same component = would form cycle
        }

        // Check if MST spans all nodes (graph is connected)
        boolean connected = (mstEdges.size() == V - 1);

        return new MSTResult(mstEdges, totalWeight, connected);
    }
}
