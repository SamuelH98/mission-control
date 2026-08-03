async function request(path, options = {}) {
  const res = await fetch(`/api${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  if (res.status === 204) {
    return null
  }

  const data = await res.json().catch(() => null)

  if (!res.ok) {
    const message = data?.message || `Request failed with status ${res.status}`
    throw new Error(message)
  }

  return data
}

export function listProjects() {
  return request('/projects')
}

export function createProject(payload) {
  return request('/projects', { method: 'POST', body: JSON.stringify(payload) })
}

export function updateProject(id, payload) {
  return request(`/projects/${id}`, { method: 'PUT', body: JSON.stringify(payload) })
}

export function deleteProject(id) {
  return request(`/projects/${id}`, { method: 'DELETE' })
}

export function listManagers() {
  return request('/managers')
}

export function createManager(payload) {
  return request('/managers', { method: 'POST', body: JSON.stringify(payload) })
}

export function deleteManager(id) {
  return request(`/managers/${id}`, { method: 'DELETE' })
}
