package com.route.controller;

import com.route.algorithm.DijkstraAlgorithm;
import com.route.algorithm.KruskalMST;
import com.route.dto.AddEdgeRequest;
import com.route.dto.GraphResponse;
import com.route.model.Node;
import com.route.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API Controller for Route Optimization.
 *
 * Endpoints:
 * POST   /api/graph/node          — Add a node
 * POST   /api/graph/edge          — Add an edge
 * GET    /api/graph               — Get current graph
 * GET    /api/graph/sample        — Load sample graph
 * DELETE /api/graph               — Reset graph
 * GET    /api/route/shortest      — Dijkstra shortest path
 * GET    /api/route/mst           — Kruskal MST
 * GET    /api/route/compare       — Naive vs Dijkstra comparison
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow React frontend (dev)
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // ─── Graph Management Endpoints ───────────────────────────────────

    @PostMapping("/graph/node")
    public ResponseEntity<String> addNode(@RequestBody Node node) {
        routeService.addNode(node);
        return ResponseEntity.ok("Node '" + node.getId() + "' added.");
    }

    @PostMapping("/graph/edge")
    public ResponseEntity<String> addEdge(@RequestBody AddEdgeRequest req) {
        routeService.addEdge(req.getSourceId(), req.getDestId(), req.getWeight());
        return ResponseEntity.ok(
            "Edge " + req.getSourceId() + " → " + req.getDestId() +
            " (weight=" + req.getWeight() + ") added."
        );
    }

    @GetMapping("/graph")
    public ResponseEntity<GraphResponse> getGraph() {
        return ResponseEntity.ok(new GraphResponse(routeService.getGraph()));
    }

    @GetMapping("/graph/sample")
    public ResponseEntity<GraphResponse> loadSample() {
        routeService.loadSampleGraph();
        return ResponseEntity.ok(new GraphResponse(routeService.getGraph()));
    }

    @DeleteMapping("/graph")
    public ResponseEntity<String> resetGraph() {
        routeService.resetGraph();
        return ResponseEntity.ok("Graph reset.");
    }

    // ─── Algorithm Endpoints ──────────────────────────────────────────

    /**
     * GET /api/route/shortest?source=A&target=F
     * Returns Dijkstra result: path, distance, visited order.
     */
    @GetMapping("/route/shortest")
    public ResponseEntity<?> getShortestPath(
            @RequestParam String source,
            @RequestParam String target) {
        try {
            DijkstraAlgorithm.DijkstraResult result =
                    routeService.findShortestPath(source, target);
            return ResponseEntity.ok(Map.of(
                "path", result.shortestPath,
                "totalDistance", result.totalDistance,
                "visitedOrder", result.visitedOrder,
                "allDistances", result.distances
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/route/mst
     * Returns MST edges and total weight.
     */
    @GetMapping("/route/mst")
    public ResponseEntity<?> getMST() {
        KruskalMST.MSTResult result = routeService.computeMST();
        return ResponseEntity.ok(Map.of(
            "mstEdges", result.mstEdges,
            "totalWeight", result.totalWeight,
            "isConnected", result.isConnected
        ));
    }

    /**
     * GET /api/route/compare?source=A&target=F
     * Returns comparison of naive BFS vs Dijkstra.
     */
    @GetMapping("/route/compare")
    public ResponseEntity<Map<String, Object>> compareRoutes(
            @RequestParam String source,
            @RequestParam String target) {
        return ResponseEntity.ok(routeService.compareRoutes(source, target));
    }
}
