package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Mpa mpa;
    private Collection<GenreDto> genres;
}
