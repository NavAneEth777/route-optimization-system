import { useEffect, useRef } from 'react'
import * as d3 from 'd3'

/**
 * Graph visualization using D3.js force simulation.
 *
 * D3 force simulation:
 * - forceLink: keeps connected nodes at target distance
 * - forceManyBody: nodes repel each other (avoid overlap)
 * - forceCenter: pulls graph to center of SVG
 *
 * Highlighted paths and MST edges are rendered in different colors.
 *
 * WHY D3?
 * - Industry standard for graph/network visualization
 * - Force-directed layout automatically positions nodes
 * - Great talking point: "used D3 force simulation for dynamic graph layout"
 */
export default function GraphCanvas({ nodes, edges, highlightedPath, mstEdges, activeAlgo }) {
  const svgRef = useRef(null)

  useEffect(() => {
    if (!nodes.length) return
    drawGraph()
  }, [nodes, edges, highlightedPath, mstEdges])

  function isEdgeInPath(edge, path) {
    if (!path || path.length < 2) return false
    for (let i = 0; i < path.length - 1; i++) {
      if (
        (edge.source.id === path[i] && edge.target.id === path[i + 1]) ||
        (edge.target.id === path[i] && edge.source.id === path[i + 1])
      ) return true
    }
    return false
  }

  function isEdgeInMST(edge, mstEdges) {
    return mstEdges.some(
      e =>
        (e.source === edge.source.id && e.destination === edge.target.id) ||
        (e.source === edge.target.id && e.destination === edge.source.id)
    )
  }

  function drawGraph() {
    const container = svgRef.current.parentElement
    const width = container.clientWidth || 600
    const height = 420

    // Clear previous
    d3.select(svgRef.current).selectAll('*').remove()

    const svg = d3.select(svgRef.current)
      .attr('width', width)
      .attr('height', height)

    // Arrow markers for directed edges
    svg.append('defs').append('marker')
      .attr('id', 'arrowhead')
      .attr('viewBox', '-0 -5 10 10')
      .attr('refX', 22)
      .attr('refY', 0)
      .attr('orient', 'auto')
      .attr('markerWidth', 6)
      .attr('markerHeight', 6)
      .append('path')
      .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
      .attr('fill', '#94a3b8')

    // Clone nodes/edges for D3 (it mutates objects)
    const d3Nodes = nodes.map(n => ({ ...n }))
    const d3Edges = edges.map(e => ({
      ...e,
      source: e.source,
      target: e.destination
    }))

    // Force simulation
    const simulation = d3.forceSimulation(d3Nodes)
      .force('link', d3.forceLink(d3Edges)
        .id(d => d.id)
        .distance(120)
      )
      .force('charge', d3.forceManyBody().strength(-400))
      .force('center', d3.forceCenter(width / 2, height / 2))
      .force('collision', d3.forceCollide().radius(35))

    // Draw edges
    const link = svg.append('g').selectAll('g').data(d3Edges).enter().append('g')

    const line = link.append('line')
      .attr('stroke-width', d => {
        if (isEdgeInPath(d, highlightedPath)) return 3.5
        if (isEdgeInMST(d, mstEdges)) return 3
        return 1.5
      })
      .attr('stroke', d => {
        if (isEdgeInPath(d, highlightedPath)) return '#3b82f6'  // blue = shortest path
        if (isEdgeInMST(d, mstEdges)) return '#10b981'           // green = MST
        return '#94a3b8'                                          // gray = default
      })
      .attr('stroke-opacity', d => {
        if (isEdgeInPath(d, highlightedPath) || isEdgeInMST(d, mstEdges)) return 1
        return 0.5
      })

    // Edge weight labels
    const edgeLabel = link.append('text')
      .text(d => d.weight)
      .attr('font-size', 11)
      .attr('fill', '#64748b')
      .attr('text-anchor', 'middle')

    // Draw nodes
    const node = svg.append('g').selectAll('g').data(d3Nodes).enter().append('g')
      .attr('cursor', 'grab')
      .call(d3.drag()
        .on('start', (event, d) => {
          if (!event.active) simulation.alphaTarget(0.3).restart()
          d.fx = d.x; d.fy = d.y
        })
        .on('drag', (event, d) => { d.fx = event.x; d.fy = event.y })
        .on('end', (event, d) => {
          if (!event.active) simulation.alphaTarget(0)
          d.fx = null; d.fy = null
        })
      )

    node.append('circle')
      .attr('r', 22)
      .attr('fill', d => {
        if (highlightedPath[0] === d.id) return '#22c55e'     // source = green
        if (highlightedPath[highlightedPath.length - 1] === d.id) return '#ef4444' // target = red
        if (highlightedPath.includes(d.id)) return '#3b82f6'  // on path = blue
        return '#1e293b'
      })
      .attr('stroke', '#334155')
      .attr('stroke-width', 2)

    // Node ID label
    node.append('text')
      .text(d => d.id)
      .attr('text-anchor', 'middle')
      .attr('dy', '0.35em')
      .attr('font-size', 13)
      .attr('font-weight', '600')
      .attr('fill', 'white')

    // Node name label (below circle)
    node.append('text')
      .text(d => d.name || '')
      .attr('text-anchor', 'middle')
      .attr('dy', '2.4em')
      .attr('font-size', 10)
      .attr('fill', '#94a3b8')

    // Tick: update positions
    simulation.on('tick', () => {
      line
        .attr('x1', d => d.source.x)
        .attr('y1', d => d.source.y)
        .attr('x2', d => d.target.x)
        .attr('y2', d => d.target.y)

      edgeLabel
        .attr('x', d => (d.source.x + d.target.x) / 2)
        .attr('y', d => (d.source.y + d.target.y) / 2 - 6)

      node.attr('transform', d => `translate(${d.x},${d.y})`)
    })
  }

  return (
    <div className="graph-canvas-wrapper">
      <svg ref={svgRef}></svg>
      <div className="legend">
        <span className="legend-item path">── Shortest Path</span>
        <span className="legend-item mst">── MST Edge</span>
        <span className="legend-item default">── Normal Edge</span>
      </div>
    </div>
  )
}
