import { useState } from 'react'

export default function ControlPanel({
  nodes, onAddNode, onAddEdge, onDijkstra,
  onMST, onCompare, onReset, onLoadSample, loading
}) {
  const [nodeId, setNodeId] = useState('')
  const [nodeName, setNodeName] = useState('')
  const [edgeSrc, setEdgeSrc] = useState('')
  const [edgeDst, setEdgeDst] = useState('')
  const [edgeWeight, setEdgeWeight] = useState('')
  const [dijkSrc, setDijkSrc] = useState('')
  const [dijkDst, setDijkDst] = useState('')
  const [activeTab, setActiveTab] = useState('build') // 'build' | 'run'

  const nodeIds = nodes.map(n => n.id)

  return (
    <div className="control-panel">
      <div className="tab-bar">
        <button
          className={activeTab === 'build' ? 'tab active' : 'tab'}
          onClick={() => setActiveTab('build')}
        >Build Graph</button>
        <button
          className={activeTab === 'run' ? 'tab active' : 'tab'}
          onClick={() => setActiveTab('run')}
        >Run Algorithms</button>
      </div>

      {activeTab === 'build' && (
        <div className="tab-content">
          <div className="quick-actions">
            <button onClick={onLoadSample} className="btn-secondary">Load Sample Graph</button>
            <button onClick={onReset} className="btn-danger">Reset</button>
          </div>

          <div className="form-group">
            <label>Add Node</label>
            <div className="input-row">
              <input placeholder="ID (e.g. A)" value={nodeId} onChange={e => setNodeId(e.target.value)} />
              <input placeholder="Name (e.g. Warehouse)" value={nodeName} onChange={e => setNodeName(e.target.value)} />
              <button onClick={() => {
                if (nodeId) { onAddNode(nodeId, nodeName || nodeId); setNodeId(''); setNodeName('') }
              }} className="btn-primary">Add</button>
            </div>
          </div>

          <div className="form-group">
            <label>Add Edge</label>
            <div className="input-row">
              <select value={edgeSrc} onChange={e => setEdgeSrc(e.target.value)}>
                <option value="">From</option>
                {nodeIds.map(id => <option key={id} value={id}>{id}</option>)}
              </select>
              <select value={edgeDst} onChange={e => setEdgeDst(e.target.value)}>
                <option value="">To</option>
                {nodeIds.map(id => <option key={id} value={id}>{id}</option>)}
              </select>
              <input
                type="number"
                placeholder="Weight"
                value={edgeWeight}
                onChange={e => setEdgeWeight(e.target.value)}
                style={{ width: 70 }}
              />
              <button onClick={() => {
                if (edgeSrc && edgeDst && edgeWeight) {
                  onAddEdge(edgeSrc, edgeDst, edgeWeight)
                  setEdgeWeight('')
                }
              }} className="btn-primary">Add</button>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'run' && (
        <div className="tab-content">
          <div className="form-group">
            <label>Shortest Path (Dijkstra)</label>
            <div className="input-row">
              <select value={dijkSrc} onChange={e => setDijkSrc(e.target.value)}>
                <option value="">Source</option>
                {nodeIds.map(id => <option key={id} value={id}>{id}</option>)}
              </select>
              <select value={dijkDst} onChange={e => setDijkDst(e.target.value)}>
                <option value="">Target</option>
                {nodeIds.map(id => <option key={id} value={id}>{id}</option>)}
              </select>
            </div>
            <div className="btn-group">
              <button
                onClick={() => dijkSrc && dijkDst && onDijkstra(dijkSrc, dijkDst)}
                className="btn-primary" disabled={loading}
              >Run Dijkstra</button>
              <button
                onClick={() => dijkSrc && dijkDst && onCompare(dijkSrc, dijkDst)}
                className="btn-secondary" disabled={loading}
              >Compare Naive vs Optimized</button>
            </div>
          </div>

          <div className="form-group">
            <label>Minimum Spanning Tree</label>
            <button onClick={onMST} className="btn-green" disabled={loading}>
              Run Kruskal's MST
            </button>
          </div>

          {loading && <div className="loading">⟳ Computing...</div>}
        </div>
      )}
    </div>
  )
}
