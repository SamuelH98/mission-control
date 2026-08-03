import { useCallback, useEffect, useRef, useState } from 'react'
import ProjectTable from './components/ProjectTable'
import ProjectForm from './components/ProjectForm'
import ManagerPanel from './components/ManagerPanel'
import { createManager, createProject, deleteManager, deleteProject, listManagers, listProjects, updateProject } from './api'

export default function App() {
  const [projects, setProjects] = useState([])
  const [managers, setManagers] = useState([])
  const [loading, setLoading] = useState(true)
  const [toast, setToast] = useState(null)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const toastTimer = useRef(null)

  const load = useCallback(async () => {
    const [projectData, managerData] = await Promise.all([listProjects(), listManagers()])
    setProjects(projectData)
    setManagers(managerData)
  }, [])

  useEffect(() => {
    load()
      .catch((err) => showToast(err.message, 'error'))
      .finally(() => setLoading(false))
  }, [load])

  function showToast(message, type = 'ok') {
    setToast({ message, type })
    clearTimeout(toastTimer.current)
    toastTimer.current = setTimeout(() => setToast(null), 3500)
  }

  async function handleSave(payload) {
    if (editing) {
      await updateProject(editing.id, payload)
      showToast('Mission updated.')
    } else {
      await createProject(payload)
      showToast('Mission launched.')
    }
    setModalOpen(false)
    setEditing(null)
    await load()
  }

  async function handleDeleteProject(project) {
    if (!window.confirm(`Delete “${project.title}”? This cannot be undone.`)) return
    try {
      await deleteProject(project.id)
      showToast('Mission scrubbed.')
      await load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  async function handleCreateManager(manager) {
    await createManager(manager)
    showToast('Manager added to the crew.')
    await load()
  }

  async function handleDeleteManager(manager) {
    if (!window.confirm(`Remove ${manager.firstName} ${manager.lastName} from the crew?`)) return
    try {
      await deleteManager(manager.id)
      showToast('Manager removed.')
      await load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  function openCreate() {
    setEditing(null)
    setModalOpen(true)
  }

  function openEdit(project) {
    setEditing(project)
    setModalOpen(true)
  }

  return (
    <div className="app">
      {toast && <div className={`toast toast--${toast.type}`}>{toast.message}</div>}

      <header className="site-header">
        <div className="site-header__inner">
          <img className="mission-badge" src="/favicon.svg" alt="Mission Control badge" />
          <div>
            <h1 className="site-title">Mission Control</h1>
            <p className="site-subtitle">Space projects portfolio tracker</p>
          </div>
        </div>
      </header>

      <section className="hero">
        <img className="hero__img" src="/saturn-crescent.jpg" alt="Saturn photographed by the James Webb Space Telescope" />
        <div className="hero__overlay">
          <p className="hero__kicker">TRACKING THE FLEET</p>
          <p className="hero__text">
            Every mission, every phase, every manager — one dashboard.
          </p>
        </div>
      </section>

      <main className="layout">
        <section className="panel panel--table">
          <div className="panel__header">
            <h2 className="panel__title">Missions</h2>
            <button className="btn btn--primary" onClick={openCreate}>
              + New Mission
            </button>
          </div>

          {loading ? (
            <p className="muted">Loading mission data…</p>
          ) : (
            <ProjectTable projects={projects} onEdit={openEdit} onDelete={handleDeleteProject} />
          )}
        </section>

        <ManagerPanel managers={managers} onCreate={handleCreateManager} onDelete={handleDeleteManager} />
      </main>

      <footer className="site-footer">
        <p>
          Imagery courtesy of NASA (public domain). Demo project — not affiliated with NASA.
        </p>
      </footer>

      {modalOpen && (
        <ProjectForm
          project={editing}
          managers={managers}
          onSave={handleSave}
          onCancel={() => {
            setModalOpen(false)
            setEditing(null)
          }}
        />
      )}
    </div>
  )
}
