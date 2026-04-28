/**
 * Displays algorithm results: Dijkstra path, MST edges, comparison.
 */
export default function ResultPanel({ result }) {
  if (!result) return null

  return (
    <div className="result-panel">
      <h3>Result</h3>

      {result.type === 'dijkstra' && (
        <div>
          <div className="result-row">
            <span className="label">Shortest Path</span>
            <span className="value path-display">{result.path?.join(' → ') || 'No path'}</span>
          </div>
          <div className="result-row">
            <span className="label">Total Distance</span>
            <span className="value highlight">{result.totalDistance}</span>
          </div>
          <div className="result-row">
            <span className="label">Nodes Visited</span>
            <span className="value">{result.visitedOrder?.length}</span>
          </div>
          <div className="visited-order">
            <span className="label">Visit Order: </span>
            {result.visitedOrder?.map((n, i) => (
              <span key={i} className="visited-node">{n}</span>
            ))}
          </div>
        </div>
      )}

      {result.type === 'mst' && (
        <div>
          <div className="result-row">
            <span className="label">MST Total Weight</span>
            <span className="value highlight green">{result.totalWeight}</span>
          </div>
          <div className="result-row">
            <span className="label">Connected</span>
            <span className={`value ${result.isConnected ? 'green' : 'red'}`}>
              {result.isConnected ? '✓ Yes' : '✗ No'}
            </span>
          </div>
          <div className="mst-edges">
            <div className="label">MST Edges:</div>
            {result.mstEdges?.map((e, i) => (
              <div key={i} className="edge-item">
                {e.source} → {e.destination} <span className="weight">({e.weight})</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {result.type === 'compare' && (
        <div>
          <div className="compare-table">
            <div className="compare-row header">
              <span>Method</span><span>Path</span><span>Cost</span>
            </div>
            <div className="compare-row optimized">
              <span>Dijkstra</span>
              <span>{result.dijkstraPath?.join('→')}</span>
              <span className="highlight">{result.dijkstraCost?.toFixed(1)}</span>
            </div>
            <div className="compare-row naive">
              <span>Naive BFS</span>
              <span>{result.naivePath?.join('→')}</span>
              <span>{result.naiveCost?.toFixed(1)}</span>
            </div>
          </div>
          <div className="savings">
            Savings: <strong>{result.savings?.toFixed(1)}</strong>
          </div>
        </div>
      )}
    </div>
  )
}
