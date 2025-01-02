package ru.yandex.practicum.filmorate.dto.film.requests;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFilmRequest {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Long mpaId;
    private List<Long> genreIds;
    private Long likes;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasMpaId() {
        return mpaId != null;
    }

    public boolean hasGenreIds() {
        return ! (genreIds == null || genreIds.isEmpty());
    }
}
