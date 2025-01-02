package ru.yandex.practicum.filmorate.storage.db.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.db.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.film.FilmRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private static final String FIND_ALL_QUERY = """
            SELECT f.*, m.name as mpa_name,
                   COUNT(l.user_id) as likes,
                   GROUP_CONCAT(g.id) as genre_ids,
                   GROUP_CONCAT(g.name) as genre_names
            FROM films f
            JOIN mpa m ON f.mpa_rating_id = m.id
            LEFT JOIN likes l ON f.id = l.film_id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            GROUP BY f.id, m.name
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO films(name, description, release_date, duration, mpa_rating_id)
            VALUES (?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
            WHERE id = ?;
            """;
    private static final String DELETE_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = ?;
            """;
    private static final String INSERT_GENRES_QUERY = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?);
            """;
    private static final String INSERT_LIKES_QUERY = """
            INSERT INTO likes (film_id, user_id)
            VALUES (?, ?);
            """;
    private static final String DELETE_LIKES_QUERY = """
            DELETE FROM likes
            WHERE film_id = ? AND user_id = ?;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*, m.name as mpa_name, COUNT(l.user_id) as likes
            FROM films f
            JOIN mpa m ON f.mpa_rating_id = m.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE f.id = ?
            GROUP BY f.id
            """;
    private static final String FIND_POPULAR_QUERY = FIND_ALL_QUERY + """
            ORDER BY likes DESC
            LIMIT ?;
            """;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaStorage mpaStorage, GenreStorage genreStorage) {
        super(jdbc, mapper);
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(film -> {
            List<Long> genreIds = genreStorage.getGenresByFilmId(film.getId()).stream()
                    .map(Genre::getId)
                    .toList();
            film.setGenreIds(genreIds);
        });
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        try {
            long id = insert(
                    INSERT_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());

            film.setId(id);
            updateFilmGenres(film);

            log.info("Film created successfully: {}", film);
            return film;
        } catch (Exception e) {
            log.error("Error creating film: {}", e.getMessage());
            throw new InternalServerException("An error occurred while creating the film.");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        try {
            update(
                    UPDATE_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );

            updateFilmGenres(film);

            Mpa updatedMpa = mpaStorage.getMpaById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA rating not found"));

            film.setMpa(updatedMpa);

            log.info("Film updated successfully: {}", film);
            return film;
        } catch (Exception e) {
            log.error("Error updating film: {}", e.getMessage());
            throw new InternalServerException("An error occurred while updating the film.");
        }
    }

    @Override
    public void updateFilmGenres(Film film) {
        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            delete(DELETE_GENRES_QUERY, film.getId());

            for (Long genreId : film.getGenreIds()) {
                try {
                    update(
                            INSERT_GENRES_QUERY,
                            film.getId(),
                            genreId
                    );
                    log.info("Adding genre: genreId={}, filmId={}", genreId, film.getId());
                } catch (DuplicateKeyException e) {
                    log.warn("Unable to add duplicate genre: genreId={}, filmId={}", genreId, film.getId());
                }
            }
        } else {
            delete(DELETE_GENRES_QUERY, film.getId());
        }
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Optional<Film> filmOptional = findOne(FIND_BY_ID_QUERY, filmId);

        filmOptional.ifPresent(film -> {
            List<Long> genreIds = genreStorage.getGenresByFilmId(filmId).stream()
                    .map(Genre::getId)
                    .toList();
            film.setGenreIds(genreIds);
        });

        return filmOptional;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.debug("Adding like: filmId={}, userId={}", filmId, userId);
        update(INSERT_LIKES_QUERY, filmId, userId);
        log.debug("Like added successfully: filmId={}", filmId);

    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.debug("Removing like: filmId={}, userId={}", filmId, userId);
        update(DELETE_LIKES_QUERY, filmId, userId);
        log.debug("Like removed successfully: filmId={}", filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return jdbc.query(FIND_POPULAR_QUERY, new FilmRowMapper(), count);
    }
}
