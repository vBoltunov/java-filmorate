package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
    public Film create(@Valid @RequestBody Film film) {
        log.info("Creating film: {}", film);

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Validation failed: Film release date is before the minimum allowed date.");
            throw new ValidationException("Film release date is before the minimum allowed date: 28.12.1895");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film created successfully: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Updating film with id: {}", newFilm.getId());

        if (newFilm.getId() == null) {
            log.warn("Validation failed: Film ID must be provided.");
            throw new ValidationException("Film ID must be provided");
        }

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.error("Film not found: id = {}", newFilm.getId());
            throw new NotFoundException("Film with id = " + newFilm.getId() + " not found");
        }

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

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