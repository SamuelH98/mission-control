import { useState } from 'react'

const EMPTY = { firstName: '', lastName: '', email: '' }

export default function ManagerPanel({ managers, onCreate, onDelete }) {
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')

  function handleChange(event) {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    try {
      await onCreate(form)
      setForm(EMPTY)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <aside className="panel">
      <h2 className="panel__title">Flight Crew</h2>

      <form className="manager-form" onSubmit={handleSubmit}>
        <div className="field-row">
          <input
            name="firstName"
            value={form.firstName}
            onChange={handleChange}
            maxLength={25}
            placeholder="First"
            required
          />
          <input
            name="lastName"
            value={form.lastName}
            onChange={handleChange}
            maxLength={25}
            placeholder="Last"
            required
          />
        </div>
        <input
          name="email"
          type="email"
          value={form.email}
          onChange={handleChange}
          maxLength={64}
          placeholder="email@nasa.gov"
          required
        />
        {error && <p className="form-error">{error}</p>}
        <button type="submit" className="btn btn--primary btn--block">
          Add Manager
        </button>
      </form>

      <ul className="manager-list">
        {managers.map((m) => (
          <li key={m.id} className="manager-item">
            <div className="manager-item__info">
              <strong>
                {m.firstName} {m.lastName}
              </strong>
              <span className="muted">{m.email}</span>
            </div>
            <button
              className="btn btn--small btn--danger"
              onClick={() => onDelete(m)}
              aria-label={`Delete ${m.firstName} ${m.lastName}`}
            >
              Delete
            </button>
          </li>
        ))}
      </ul>
    </aside>
  )
}
