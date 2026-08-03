import { useEffect, useState } from 'react'

const EMPTY = { title: '', description: '', status: 'planned', managerId: '' }

export default function ProjectForm({ project, managers, onSave, onCancel }) {
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')

  useEffect(() => {
    if (project) {
      setForm({
        title: project.title,
        description: project.description || '',
        status: project.status,
        managerId: project.managerId ?? '',
      })
    } else {
      setForm(EMPTY)
    }
    setError('')
  }, [project])

  function handleChange(event) {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    if (!form.managerId) {
      setError('Pick a mission manager.')
      return
    }
    try {
      await onSave({ ...form, managerId: Number(form.managerId) })
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onCancel}>
      <div className="modal" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
        <h2 className="modal__title">{project ? 'Update Mission' : 'New Mission'}</h2>
        <form onSubmit={handleSubmit}>
          <label className="field">
            <span>Title</span>
            <input
              name="title"
              value={form.title}
              onChange={handleChange}
              maxLength={50}
              required
              placeholder="e.g. Artemis III Landing"
            />
            <small>{form.title.length}/50</small>
          </label>

          <label className="field">
            <span>Description</span>
            <textarea
              name="description"
              value={form.description}
              onChange={handleChange}
              maxLength={500}
              rows={4}
              placeholder="What is this mission about?"
            />
            <small>{form.description.length}/500</small>
          </label>

          <div className="field-row">
            <label className="field">
              <span>Status</span>
              <select name="status" value={form.status} onChange={handleChange}>
                <option value="planned">Planned</option>
                <option value="active">Active</option>
                <option value="completed">Completed</option>
              </select>
            </label>

            <label className="field">
              <span>Manager</span>
              <select name="managerId" value={form.managerId} onChange={handleChange} required>
                <option value="" disabled>
                  Choose…
                </option>
                {managers.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.firstName} {m.lastName}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {error && <p className="form-error">{error}</p>}

          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={onCancel}>
              Cancel
            </button>
            <button type="submit" className="btn btn--primary">
              {project ? 'Save Changes' : 'Launch Mission'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
