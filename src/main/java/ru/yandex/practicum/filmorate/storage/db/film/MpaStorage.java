package ru.yandex.practicum.filmorate.storage.db.film;

import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> getMpas();

    Optional<Mpa> getMpaById(Long id);
}