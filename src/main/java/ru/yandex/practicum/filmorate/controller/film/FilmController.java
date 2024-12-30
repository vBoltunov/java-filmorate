package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.requests.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.requests.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private static final String LIKES_ENDPOINT = "/{film-id}/like";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getFilms() {
        log.info("Fetching all films.");
        return filmService.getFilms();
    }

    @GetMapping("/{film-id}")
    @ResponseStatus(HttpStatus.OK)
    public FilmDto getFilmById(@PathVariable("film-id") Long filmId) {
        log.info("Fetching film with id {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody NewFilmRequest filmRequest) {
        log.info("Creating new film: {}", filmRequest);
        return filmService.createFilm(filmRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto updateFilm(@RequestBody UpdateFilmRequest request) {
        Long filmId = request.getId();
        if (filmId == null) {
            throw new IllegalArgumentException("Film ID is required");
        }
        log.info("Updating film with id: {}", filmId);
        return filmService.updateFilm(filmId, request);
    }

    @PutMapping(LIKES_ENDPOINT + "/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable("film-id") Long filmId, @PathVariable("user-id") Long userId) {
        log.info("Adding like: filmId={}, userId={}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(LIKES_ENDPOINT + "/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable("film-id") Long filmId, @PathVariable("user-id") Long userId) {
        log.info("Removing like: filmId={}, userId={}", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Fetching popular films.");
        return filmService.getPopularFilms(count);
    }
}