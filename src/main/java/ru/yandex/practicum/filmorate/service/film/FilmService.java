package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SortOrder;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private void validateFilmExist(Long filmId) {
        getFilmByIdOrThrow(filmId);
    }

    private void validateUserExist(Long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
    }

    private Film getFilmByIdOrThrow(Long filmId) {
        return filmStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Film with id = " + filmId + " not found"));
    }

    private void initializeLikes(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Adding like: filmId={}, userId={}", filmId, userId);
        validateFilmExist(filmId);
        validateUserExist(userId);

        Film film = getFilmByIdOrThrow(filmId);
        initializeLikes(film);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);

        log.debug("Like added successfully: film={}", film);
    }

    // Add logging
    public void removeLike(Long filmId, Long userId) {
        log.debug("Removing like: filmId={}, userId={}", filmId, userId);
        validateFilmExist(filmId);
        validateUserExist(userId);

        Film film = getFilmByIdOrThrow(filmId);
        initializeLikes(film);
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);

        log.debug("Like removed successfully: film={}", film);
    }

    public Collection<Film> showPopular(int from, int size, SortOrder sort) {
        return filmStorage.getFilms().stream()
                .sorted((p1, p2) -> sort == SortOrder.ASCENDING ?
                        Integer.compare(p1.getLikes().size(), p2.getLikes().size()) :
                        Integer.compare(p2.getLikes().size(), p1.getLikes().size()))
                .skip(from)
                .limit(size)
                .toList();
    }
}