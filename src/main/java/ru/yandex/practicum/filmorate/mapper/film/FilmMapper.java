package ru.yandex.practicum.filmorate.mapper.film;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.requests.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.requests.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (request.getGenres() != null) {
            film.setGenreIds(request.getGenres().stream()
                    .map(GenreDto::getId)
                    .toList());
        } else {
            film.setGenreIds(new ArrayList<>());
        }

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(new Mpa(film.getMpa().getId(), film.getMpa().getName()));

        List<GenreDto> genres = film.getGenreIds() != null
                ? film.getGenreIds().stream()
                .map(id -> new GenreDto(id, null))
                .toList()
                : new ArrayList<>();

        dto.setGenres(genres);
        dto.setLikes(film.getLikes());

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasLikes()) {
            film.setLikes(request.getLikes());
        }
        return film;
    }
}
