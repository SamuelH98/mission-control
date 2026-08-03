export default function StatsBand({ projects }) {
  const stats = [
    { label: 'Total Missions', value: projects.length },
    { label: 'Active', value: projects.filter((p) => p.status === 'active').length },
    { label: 'Completed', value: projects.filter((p) => p.status === 'completed').length },
    { label: 'Planned', value: projects.filter((p) => p.status === 'planned').length },
  ]

  return (
    <section className="stats">
      <img
        className="stats__img"
        src="/jupiter-storms.jpg"
        alt="Jupiter's storms, photographed by NASA"
      />
      <div className="stats__scrim" />
      <div className="stats__inner">
        {stats.map((s) => (
          <div key={s.label} className="stat">
            <span className="stat__value">{s.value}</span>
            <span className="stat__label">{s.label}</span>
          </div>
        ))}
      </div>
    </section>
  )
}
