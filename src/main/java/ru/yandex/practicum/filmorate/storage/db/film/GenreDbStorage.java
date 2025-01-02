package ru.yandex.practicum.filmorate.storage.db.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.db.BaseDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = """
        SELECT *
        FROM genres;
        """;
    private static final String FIND_BY_ID_QUERY = """
        SELECT *
        FROM genres
        WHERE id = ?;
        """;
    private static final String FIND_FILM_GENRES_QUERY = """
        SELECT g.id, g.name
        FROM genres g
        JOIN film_genres fg ON g.id = fg.genre_id
        WHERE fg.film_id = ?;
        """;


    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> getGenreById(Long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        return jdbc.query(FIND_FILM_GENRES_QUERY, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }
}
