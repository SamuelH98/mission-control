# PROJECT_STATE

## Project Overview

### Project Name
Mission Control — projects CRUD tracker (NASA themed)

### Goal
A data-driven CRUD app for a "projects" table backed by SQLite, with a Java backend and a React frontend, shipped with a Docker image and a human-readable README.

### Current Status
Complete. All requirements implemented, tested, validated end-to-end (local + Docker), committed.

---

## Completed Features

### Feature: Backend REST API (Spring Boot + SQLite)
- Full CRUD for `/api/projects` and `/api/managers`.
- Schema per spec: `projects(id, title varchar(50), description varchar(500), status enum, manager_id FK)`, `managers(id, first_name, last_name, email)`.
- Status enum restricted to `active|completed|planned` via DB CHECK + Java enum.
- Validation with jakarta constraints (lengths, required fields, email), 400 with field errors.
- 404 for missing resources, 409 for duplicate email / deleting a referenced manager.
- Seed data (NASA-flavored) via `data.sql`, idempotent on restart.

#### Validation
- `./mvnw test` — 24 tests pass (ProjectStatusTest, ProjectControllerTest, ManagerControllerTest).
- Live smoke test via curl: create/read/update/delete, 400 on bad status, 409 on duplicate email, 404 on missing id, all correct.
- `./mvnw package` builds a runnable jar.

#### Tests Added
- `ProjectStatusTest` — enum parsing, case-insensitivity, rejection of unknown values.
- `ProjectControllerTest` — list, get, create, update, delete, validation failures, unknown manager.
- `ManagerControllerTest` — CRUD, invalid email, duplicate email, referenced-manager delete block.

### Feature: Frontend (React + Vite, NASA themed)
- Mission dashboard: projects table, status badges, create/edit modal, delete with confirm, flight-crew (manager) sidebar with add/delete.
- Toast notifications for success/error.
- No CDNs — NASA imagery (`saturn-crescent.jpg`, `jupiter-storms.jpg`) downloaded into `frontend/public/`, custom SVG favicon.
- Vite dev proxy `/api` -> localhost:8080.

#### Validation
- `npm run lint` (oxlint) clean.
- `npm run build` succeeds.
- Live: dev server on :5173 served the app and proxied `/api` to the backend.

#### Tests Added
- None (frontend is thin UI glue over the API; exercised via live e2e instead). Lint + build gates apply.

### Feature: Docker
- Multi-stage `Dockerfile`: node build -> maven build (frontend bundled into jar static resources) -> `eclipse-temurin:17-jre` runtime.
- Single image serves UI + API + static assets on :8080; DB persisted via `/app/data` volume.
- `docker-compose.yml` for one-command startup.

#### Validation
- `docker build` succeeds.
- Container run: `/api/projects` 200, `/` serves index.html, `/saturn-crescent.jpg` 200.

### Feature: README + packaging
- Human, plain-language README (run instructions, API table, schema, structure).
- Maven wrapper committed so no local Maven needed.

---

## Current Work

### Active Feature
None — work complete.

### Progress
N/A

### Remaining Work
N/A

---

## Next Actions

1. (Optional) Add a frontend unit test for the form or a small e2e test.
2. (Optional) GitHub Actions pipeline building backend tests + Docker image.

---

## Risks

### Open Questions
- None outstanding.

### Known Issues
- None known. Minor: backend pools a single SQLite connection by design (documented in README).

### Technical Concerns
- SQLite + Hikari: pool size forced to 1 to avoid "database is locked" on writes. Fine for this scale.
- SQLite unique-violation exception is not translated by Spring to `DataIntegrityViolationException`; handled via service pre-check + a `SQLiteException` root-cause handler.
- Machine used for dev has JRE 21 + JDK 17 only (no JDK 21); project targets Java 17 to match. Runs fine on any JRE >= 17.

---

## Troubleshooting Log

### Problem: `error: release version 21 not supported`
- **Attempts tried:** Set JAVA_HOME to the 21 JRE; re-ran with JDK 21 path — persisted.
- **Sources checked (web/docs):** `ls /usr/lib/jvm/*`, `dpkg -l | grep jdk`.
- **Current hypothesis:** The only full JDK on this machine is 17; `openjdk-21` is JRE-only (no javac), so in-process javac is 17.
- **Status:** resolved — targeted Java 17 for the project (Spring Boot 3.5 fully supports it; bytecode runs on JRE 21+).

---

## Resume Instructions

Start by reading `README.md` — it has the full run instructions.

- Backend entry point: `backend/src/main/java/com/missioncontrol/`. Controllers -> services -> JDBC repositories; schema/seeds in `backend/src/main/resources/`.
- Frontend entry point: `frontend/src/App.jsx`; API client in `frontend/src/api.js`.
- Verify current state:
  ```bash
  cd backend && ./mvnw test
  cd frontend && npm run lint && npm run build
  docker build -t mission-control .   # optional
  ```
- Single next step if picking this up: add a GitHub Actions workflow, or a small frontend test for the ProjectForm component.
