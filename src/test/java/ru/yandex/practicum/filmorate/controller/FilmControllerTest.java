package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.util.ValidationUtil;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void setUp() {
        film = new Film();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        filmController = new FilmController(filmStorage);
    }

    @Test
    void createFilm_WithEmptyName() {
        film.setName("");
        film.setDescription("A valid description");
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = ValidationUtil.validate(film);

        assertFalse(violations.isEmpty(), "Expected validation violations but none found.");
        assertEquals("Film name must not be empty", ValidationUtil.getFirstViolationMessage(violations));
    }

    @Test
    void createFilm_WithPastReleaseDate() {
        film.setName("Valid Name");
        film.setDescription("A valid description");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));

        assertEquals("Film release date is before the minimum allowed date: 28.12.1895", exception.getMessage());
    }

    @Test
    void createFilm_WithLongDescription() {
        film.setName("Valid Name");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = ValidationUtil.validate(film);

        assertFalse(violations.isEmpty(), "Expected validation violations but none found.");
        assertEquals("Description is too long. Maximum length: 200",
                ValidationUtil.getFirstViolationMessage(violations));
    }

    @Test
    void createFilm_WithNegativeDuration() {
        film.setName("Valid Name");
        film.setDescription("A valid description");
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = ValidationUtil.validate(film);

        assertFalse(violations.isEmpty(), "Expected validation violations but none found.");
        assertEquals("Duration should be a positive number", ValidationUtil.getFirstViolationMessage(violations));
    }
}