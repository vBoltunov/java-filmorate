package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    long duration;
}
