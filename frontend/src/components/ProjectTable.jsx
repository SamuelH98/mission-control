import StatusBadge from './StatusBadge'

export default function ProjectTable({ projects, onEdit, onDelete }) {
  if (projects.length === 0) {
    return (
      <div className="empty-state">
        <p>No missions on the board.</p>
        <p className="empty-state__sub">Use “New Mission” to add the first one.</p>
      </div>
    )
  }

  return (
    <div className="table-wrap">
      <table className="project-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Mission</th>
            <th>Status</th>
            <th>Manager</th>
            <th aria-label="actions"></th>
          </tr>
        </thead>
        <tbody>
          {projects.map((p) => (
            <tr key={p.id}>
              <td className="cell-id">MC-{String(p.id).padStart(3, '0')}</td>
              <td>
                <div className="cell-title">
                  <strong>{p.title}</strong>
                  {p.description && <span className="cell-desc">{p.description}</span>}
                </div>
              </td>
              <td>
                <StatusBadge status={p.status} />
              </td>
              <td>{p.managerName || <em className="muted">—</em>}</td>
              <td className="cell-actions">
                <button className="btn btn--small" onClick={() => onEdit(p)}>
                  Edit
                </button>
                <button className="btn btn--small btn--danger" onClick={() => onDelete(p)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
