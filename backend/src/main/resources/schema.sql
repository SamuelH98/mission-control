CREATE TABLE IF NOT EXISTS managers (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR(25) NOT NULL,
    last_name  VARCHAR(25) NOT NULL,
    email      VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS projects (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    title       VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL DEFAULT '',
    status      TEXT NOT NULL CHECK (status IN ('active', 'completed', 'planned')),
    manager_id  INTEGER,
    FOREIGN KEY (manager_id) REFERENCES managers (id)
);

CREATE INDEX IF NOT EXISTS idx_projects_manager_id ON projects (manager_id);
