package ru.yandex.practicum.filmorate.dto.film.requests;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.film.GenreDto;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Mpa mpa;
    private List<GenreDto> genres;
}
