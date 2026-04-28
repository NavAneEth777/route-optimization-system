import { useState, useEffect } from 'react'
import GraphCanvas from './components/GraphCanvas'
import ControlPanel from './components/ControlPanel'
import ResultPanel from './components/ResultPanel'
import './styles/App.css'

/**
 * Root component for Route Optimization System.
 *
 * State architecture:
 * - graphData: nodes + edges from backend
 * - result: Dijkstra / MST / comparison result
 * - highlightedPath: edges to highlight in canvas
 * - loading / error: async state
 */
export default function App() {
  const [graphData, setGraphData] = useState({ nodes: [], edges: [] })
  const [result, setResult] = useState(null)
  const [highlightedPath, setHighlightedPath] = useState([])
  const [mstEdges, setMstEdges] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [activeAlgo, setActiveAlgo] = useState(null) // 'dijkstra' | 'mst' | 'compare'

  const API = 'http://localhost:8080/api'

  // Load sample graph on mount
  useEffect(() => {
    fetchSampleGraph()
  }, [])

  async function fetchSampleGraph() {
    setLoading(true)
    try {
      const res = await fetch(`${API}/graph/sample`)
      const data = await res.json()
      setGraphData(data)
      setError(null)
    } catch (e) {
      setError('Backend not running. Start Spring Boot on port 8080.')
    } finally {
      setLoading(false)
    }
  }

  async function fetchGraph() {
    const res = await fetch(`${API}/graph`)
    const data = await res.json()
    setGraphData(data)
  }

  async function addNode(id, name) {
    await fetch(`${API}/graph/node`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id, name })
    })
    fetchGraph()
  }

  async function addEdge(sourceId, destId, weight) {
    await fetch(`${API}/graph/edge`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sourceId, destId, weight: parseFloat(weight) })
    })
    fetchGraph()
  }

  async function runDijkstra(source, target) {
    setLoading(true)
    setActiveAlgo('dijkstra')
    try {
      const res = await fetch(`${API}/route/shortest?source=${source}&target=${target}`)
      const data = await res.json()
      setResult({ type: 'dijkstra', ...data })
      setHighlightedPath(data.path || [])
      setMstEdges([])
    } catch (e) {
      setError('Dijkstra failed: ' + e.message)
    } finally {
      setLoading(false)
    }
  }

  async function runMST() {
    setLoading(true)
    setActiveAlgo('mst')
    try {
      const res = await fetch(`${API}/route/mst`)
      const data = await res.json()
      setResult({ type: 'mst', ...data })
      setMstEdges(data.mstEdges || [])
      setHighlightedPath([])
    } catch (e) {
      setError('MST failed: ' + e.message)
    } finally {
      setLoading(false)
    }
  }

  async function runCompare(source, target) {
    setLoading(true)
    setActiveAlgo('compare')
    try {
      const res = await fetch(`${API}/route/compare?source=${source}&target=${target}`)
      const data = await res.json()
      setResult({ type: 'compare', ...data })
      setHighlightedPath(data.dijkstraPath || [])
    } finally {
      setLoading(false)
    }
  }

  async function resetGraph() {
    await fetch(`${API}/graph`, { method: 'DELETE' })
    setGraphData({ nodes: [], edges: [] })
    setResult(null)
    setHighlightedPath([])
    setMstEdges([])
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>Graph Route Optimization</h1>
        <p className="subtitle">Dijkstra • MST (Kruskal) • Greedy Comparison</p>
      </header>

      {error && (
        <div className="error-banner">
          ⚠ {error}
          <button onClick={() => setError(null)}>×</button>
        </div>
      )}

      <div className="app-body">
        {/* LEFT: Graph Visualization */}
        <section className="canvas-section">
          <GraphCanvas
            nodes={graphData.nodes}
            edges={graphData.edges}
            highlightedPath={highlightedPath}
            mstEdges={mstEdges}
            activeAlgo={activeAlgo}
          />
          <div className="graph-stats">
            <span>Nodes: {graphData.nodeCount ?? graphData.nodes.length}</span>
            <span>Edges: {graphData.edgeCount ?? graphData.edges.length}</span>
          </div>
        </section>

        {/* RIGHT: Controls + Results */}
        <aside className="sidebar">
          <ControlPanel
            nodes={graphData.nodes}
            onAddNode={addNode}
            onAddEdge={addEdge}
            onDijkstra={runDijkstra}
            onMST={runMST}
            onCompare={runCompare}
            onReset={resetGraph}
            onLoadSample={fetchSampleGraph}
            loading={loading}
          />
          {result && <ResultPanel result={result} />}
        </aside>
      </div>
    </div>
  )
}
