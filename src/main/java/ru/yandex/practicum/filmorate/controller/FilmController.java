package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Fetching all films.");
        return filmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Film>> getFilm(@PathVariable Long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        return ResponseEntity.ok(film);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Film createdFilm = filmStorage.createFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = filmStorage.updateFilm(film);
        return ResponseEntity.ok(updatedFilm);
    }
}