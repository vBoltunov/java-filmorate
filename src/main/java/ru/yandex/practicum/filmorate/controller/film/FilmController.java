package ru.yandex.practicum.filmorate.controller.film;

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
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.enums.SortOrder;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.db.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

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

    @GetMapping("/popular")
    public Collection<Film> showPopular(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort) {

        SortOrder sortOrder = SortOrder.sortOrder(sort);
        if (sortOrder == null) {
            throw new IllegalArgumentException("Invalid sort order: " + sort);
        }
        if (size < 0) {
            throw new IllegalArgumentException("Requested collection size must be greater than 0");
        }
        if (from < 0) {
            throw new IllegalArgumentException("Starting position must be greater than 0");
        }

        return filmService.showPopular(from, size, sortOrder);
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

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Adding friend with id: {} to user with id: {}", userId, id);
        filmService.addLike(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Removing like: filmId={}, userId={}", id, userId);
        filmService.removeLike(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}