package com.missioncontrol.repository;

import com.missioncontrol.model.ProjectRequest;
import com.missioncontrol.model.ProjectResponse;
import com.missioncontrol.model.ProjectStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {

    private static final String SELECT =
            "SELECT p.id, p.title, p.description, p.status, p.manager_id, "
                    + "(m.first_name || ' ' || m.last_name) AS manager_name "
                    + "FROM projects p LEFT JOIN managers m ON m.id = p.manager_id ";

    private static final RowMapper<ProjectResponse> ROW_MAPPER =
            (rs, rowNum) -> new ProjectResponse(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    ProjectStatus.fromDb(rs.getString("status")),
                    (Integer) rs.getObject("manager_id"),
                    rs.getString("manager_name"));

    private final JdbcTemplate jdbc;

    public ProjectRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ProjectResponse> findAll() {
        return jdbc.query(SELECT + "ORDER BY p.id", ROW_MAPPER);
    }

    public Optional<ProjectResponse> findById(int id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(SELECT + "WHERE p.id = ?", ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM projects WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public ProjectResponse insert(ProjectRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO projects (title, description, status, manager_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.title().trim());
            ps.setString(2, request.description() == null ? "" : request.description().trim());
            ps.setString(3, request.status().getValue());
            ps.setInt(4, request.managerId());
            return ps;
        }, keyHolder);
        return findById(keyHolder.getKey().intValue()).orElseThrow();
    }

    public boolean update(int id, ProjectRequest request) {
        return jdbc.update(
                "UPDATE projects SET title = ?, description = ?, status = ?, manager_id = ? WHERE id = ?",
                request.title().trim(),
                request.description() == null ? "" : request.description().trim(),
                request.status().getValue(),
                request.managerId(),
                id) > 0;
    }

    public boolean delete(int id) {
        return jdbc.update("DELETE FROM projects WHERE id = ?", id) > 0;
    }
}
