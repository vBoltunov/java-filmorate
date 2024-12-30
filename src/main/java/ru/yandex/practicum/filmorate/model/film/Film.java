package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Represents a film with its essential details.
 *
 * This class includes attributes such as the film's id, name, description, release date, duration, MAP rating,
 * list of users' IDs who liked the film and list of film's genre IDs.
 * It uses validation annotations to ensure that the film's name is not empty, the description does not exceed 200 characters,
 * the release date is not null, and the duration is a positive number.
 * It uses the `@Data` annotation to automatically generate boilerplate code like getters, setters, and constructors.
 */
@Data
public class Film {
    Long id;

    @NotBlank(message = "Film name must not be empty")
    String name;

    @Size(max = 200, message = "Description is too long. Maximum length: 200")
    String description;

    @NotNull(message = "Release date must not be null")
    LocalDate releaseDate;

    @Positive(message = "Duration should be a positive number")
    Long duration;

    Mpa mpa;

    Set<Long> likes;

    List<Long> genreIds;
}