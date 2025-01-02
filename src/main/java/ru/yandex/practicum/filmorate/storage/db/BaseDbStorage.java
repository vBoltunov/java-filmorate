package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    public BaseDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = null;
    }

    public BaseDbStorage(JdbcTemplate jdbc, RowMapper<T> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            return mapper != null
                    ? Optional.ofNullable(jdbc.queryForObject(query, mapper, params))
                    : Optional.empty();
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return mapper != null
                ? jdbc.query(query, mapper, params)
                : Collections.emptyList();
    }

    public void delete(String query, long id) {
        jdbc.update(query, id);
    }

    protected long insert(String query, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Failed to save data");
        }
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Failed to update data");
        }
    }
}