package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.time.LocalDate;
import java.util.Collection;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    long duration;
    Mpa mpa;
    Collection<GenreDto> genres;
    Long likes;
}
