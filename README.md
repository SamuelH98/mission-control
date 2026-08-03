# Mission Control

A small CRUD app for tracking "projects" (space missions, in theme). React frontend, Java/Spring Boot backend, SQLite database. Everything runs locally with no CDNs — the NASA imagery is downloaded into the repo.

## What's in here

```
interview-exercise/
├── backend/     Spring Boot API (Java 17), talks to SQLite
├── frontend/    React + Vite single-page app
├── Dockerfile   Builds one image with the whole thing
└── docker-compose.yml
```

The database has two tables. A project belongs to one manager.

```
projects
  id          integer, primary key, auto-increment
  title       varchar(50)
  description varchar(500)
  status      'active' | 'completed' | 'planned'
  manager_id  foreign key -> managers.id

managers
  id          integer, primary key, auto-increment
  first_name  varchar(25)
  last_name   varchar(25)
  email       varchar(64)
```

## The easy way: Docker

You need Docker installed. That's it.

```bash
docker build -t mission-control .
docker run -p 8080:8080 mission-control
```

Open http://localhost:8080. The image builds the frontend, packages it into the backend jar, and runs one process on port 8080. The SQLite file lives in a volume at `/app/data` inside the container, so your data survives restarts.

If you have Docker Compose:

```bash
docker compose up --build
```

## Running it locally (for development)

You need:

- **Java 17+** (JDK, not just a JRE — you need `javac`)
- **Maven 3.9+**
- **Node 20+**

**1. Start the backend**

```bash
cd backend
mvn spring-boot:run
```

Wait for "Started MissionControlApplication". The API is now on http://localhost:8080. On first start it creates the database and seeds a few managers and projects.

The database file lands in `backend/data/`. Delete that folder to start fresh.

**2. Start the frontend** (in another terminal)

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The dev server proxies `/api` to the backend on 8080, so everything just works.

## API

All endpoints are under `/api` and use JSON.

| Method | Path                  | What it does                        |
|--------|-----------------------|-------------------------------------|
| GET    | /api/projects         | List all projects                   |
| GET    | /api/projects/{id}    | Get one project                     |
| POST   | /api/projects         | Create a project                    |
| PUT    | /api/projects/{id}    | Update a project                    |
| DELETE | /api/projects/{id}    | Delete a project                    |
| GET    | /api/managers         | List all managers                   |
| GET    | /api/managers/{id}    | Get one manager                     |
| POST   | /api/managers         | Create a manager                    |
| PUT    | /api/managers/{id}    | Update a manager                    |
| DELETE | /api/managers/{id}    | Delete a manager (fails if in use)  |

Example create:

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{"title":"Artemis III Landing","description":"Crewed return to the lunar south pole.","status":"planned","managerId":2}'
```

Validation rules: title required (≤50 chars), description ≤500, status must be one of the three values, manager must exist. Bad input returns a `400` with details; missing resources return `404`; things like duplicate emails or deleting a manager that still owns projects return `409`.

## Running the tests

```bash
cd backend
mvn test
```

These are Spring Boot integration tests (MockMvc) against a throwaway SQLite file — CRUD happy paths, validation failures, and the constraint cases.

## Project structure notes

- `backend/` is a plain Spring Boot app: controllers → services → JDBC repositories. The schema lives in `backend/src/main/resources/schema.sql` and seeds in `data.sql`.
- `frontend/` is a Vite/React app with no UI framework — plain CSS, a few components, and `fetch`. The NASA photos are in `frontend/public/`.

The NASA images are public domain, used here with credit. This project isn't affiliated with NASA — it's a themed demo.

## Known small things

- The backend pools a single SQLite connection (SQLite locks writes, so this is deliberate and plenty for this app).
- Deleting a manager that's assigned to a project is blocked by design — move or delete the project first.
