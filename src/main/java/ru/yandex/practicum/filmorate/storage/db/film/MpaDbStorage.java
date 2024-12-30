package ru.yandex.practicum.filmorate.storage.db.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.db.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    private static final String FIND_ALL_QUERY = """
        SELECT *
        FROM mpa;
        """;
    private static final String FIND_BY_ID_QUERY = """
        SELECT *
        FROM mpa
        WHERE id = ?;
        """;

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getMpas() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> getMpaById(Long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }
}
