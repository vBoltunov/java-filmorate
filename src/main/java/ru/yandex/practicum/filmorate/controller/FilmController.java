package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Fetching all films.");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Creating film: {}", film);

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Validation failed: Film name must not be empty.");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Validation failed: Description is too long. Maximum length: 200.");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Validation failed: Film release date is before the minimum allowed date.");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }

        if (film.getDuration() < 0) {
            log.warn("Validation failed: Duration should be a positive number.");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film created successfully: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Updating film with id: {}", newFilm.getId());

        if (newFilm.getId() == null) {
            log.warn("Validation failed: Film ID must be provided.");
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.error("Film not found: id = {}", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() > 0) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        log.info("Film updated successfully: {}", oldFilm);

        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

