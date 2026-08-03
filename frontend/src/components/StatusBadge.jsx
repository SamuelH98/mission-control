const LABELS = {
  active: 'Active',
  completed: 'Completed',
  planned: 'Planned',
}

export default function StatusBadge({ status }) {
  return <span className={`badge badge--${status}`}>{LABELS[status] || status}</span>
}
