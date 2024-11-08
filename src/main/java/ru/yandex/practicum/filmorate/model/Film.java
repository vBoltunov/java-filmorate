package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    long duration;
}
