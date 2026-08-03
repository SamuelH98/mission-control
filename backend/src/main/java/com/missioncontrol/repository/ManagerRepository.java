package com.missioncontrol.repository;

import com.missioncontrol.model.Manager;
import com.missioncontrol.model.ManagerRequest;
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
public class ManagerRepository {

    private static final String COLUMNS = "id, first_name, last_name, email";
    private static final RowMapper<Manager> ROW_MAPPER =
            (rs, rowNum) -> new Manager(rs.getInt("id"), rs.getString("first_name"),
                    rs.getString("last_name"), rs.getString("email"));

    private final JdbcTemplate jdbc;

    public ManagerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Manager> findAll() {
        return jdbc.query("SELECT " + COLUMNS + " FROM managers ORDER BY last_name, first_name", ROW_MAPPER);
    }

    public Optional<Manager> findById(int id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT " + COLUMNS + " FROM managers WHERE id = ?", ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Manager> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    "SELECT " + COLUMNS + " FROM managers WHERE email = ?", ROW_MAPPER, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Manager insert(ManagerRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO managers (first_name, last_name, email) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.firstName().trim());
            ps.setString(2, request.lastName().trim());
            ps.setString(3, request.email().trim());
            return ps;
        }, keyHolder);
        return findById(keyHolder.getKey().intValue()).orElseThrow();
    }

    public boolean update(int id, ManagerRequest request) {
        return jdbc.update(
                "UPDATE managers SET first_name = ?, last_name = ?, email = ? WHERE id = ?",
                request.firstName().trim(), request.lastName().trim(), request.email().trim(), id) > 0;
    }

    public boolean delete(int id) {
        return jdbc.update("DELETE FROM managers WHERE id = ?", id) > 0;
    }

    public boolean isReferencedByProject(int managerId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM projects WHERE manager_id = ?", Integer.class, managerId);
        return count != null && count > 0;
    }
}
