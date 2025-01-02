package ru.yandex.practicum.filmorate.service.film;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.MpaDto;
import ru.yandex.practicum.filmorate.dto.film.requests.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.requests.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.db.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.db.film.MpaStorage;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;
import ru.yandex.practicum.filmorate.util.FormatUtil;
import ru.yandex.practicum.filmorate.util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final String MIN_RELEASE_TEMPLATE = "Film release date is before the minimum allowed date: 28.12.1895";

    public FilmDto createFilm(NewFilmRequest request) {
        try {
            Mpa mpa = validateAndFetchMpa(request.getMpa().getId());
            Film film = FilmMapper.mapToFilm(request);

            film.setMpa(mpa);
            validateFilm(film);

            film = filmStorage.createFilm(film);
            List<GenreDto> genres = getFilmGenres(film.getId());

            FilmDto filmDto = FilmMapper.mapToFilmDto(film);
            filmDto.setGenres(genres);

            return filmDto;
        } catch (RuntimeException e) {
            log.error("Error creating film: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Data integrity violation: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error creating film: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void validateFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        Set<ConstraintViolation<Film>> violations = ValidationUtil.validate(film);
        if (!violations.isEmpty()) {
            throw new ConditionsNotMetException(ValidationUtil.getFirstViolationMessage(violations));
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new IllegalArgumentException(MIN_RELEASE_TEMPLATE);
        }
    }

    public FilmDto updateFilm(Long filmId, UpdateFilmRequest request) {
        try {
            Film existingFilm = filmStorage.getFilmById(filmId)
                    .orElseThrow(() -> new NotFoundException(
                            FormatUtil.formatNotFoundMessage("Film", filmId)));
            Film updatedFilm = FilmMapper.updateFilmFields(existingFilm, request);
            if (request.hasMpaId()) {
                Mpa mpa = validateAndFetchMpa(request.getMpaId());
                updatedFilm.setMpa(mpa);
            }
            if (request.hasGenreIds()) {
                updatedFilm.setGenreIds(request.getGenreIds());
            }
            if (request.hasLikes()) {
                updatedFilm.setLikes(request.getLikes());
            }
            updatedFilm = filmStorage.updateFilm(updatedFilm);
            return FilmMapper.mapToFilmDto(updatedFilm);
        } catch (Exception e) {
            log.error("Error updating film with id {}: {}", filmId, e.getMessage(), e);
            throw e;
        }
    }

    public FilmDto getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .map(this::mapToFilmDtoWithGenres)
                .orElseThrow(() -> new NotFoundException(
                        FormatUtil.formatNotFoundMessage("Film", filmId)));
    }

    private FilmDto mapToFilmDtoWithGenres(Film film) {
        FilmDto filmDto = FilmMapper.mapToFilmDto(film);
        List<GenreDto> fullGenres = getFullGenres(film.getGenreIds());
        filmDto.setGenres(fullGenres);
        return filmDto;
    }

    private List<GenreDto> getFullGenres(List<Long> genreIds) {
        return Optional.ofNullable(genreIds)
                .map(ids -> ids.stream()
                        .map(this::getGenreDtoById)
                        .toList())
                .orElseGet(ArrayList::new);
    }

    private GenreDto getGenreDtoById(Long genreId) {
        Genre genre = genreStorage.getGenreById(genreId)
                .orElse(new Genre(genreId, "Unknown"));
        return new GenreDto(genre.getId(), genre.getName());
    }

    public List<FilmDto> getFilms() {
        return filmStorage.getFilms()
                .stream()
                .map(this::mapToFilmDtoWithGenres)
                .toList();
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Removing like: filmId={}, userId={}", filmId, userId);
        if (userStorage.getUserById(userId).isPresent() && filmStorage.getFilmById(filmId).isPresent()) {
            filmStorage.removeLike(filmId, userId);
            log.debug("Like removed successfully: filmId={}", filmId);
        } else {
            if (userStorage.getUserById(userId).isEmpty()) {
                throw new NotFoundException(FormatUtil.formatNotFoundMessage("User", userId));
            }
            if (filmStorage.getFilmById(filmId).isEmpty()) {
                throw new NotFoundException(FormatUtil.formatNotFoundMessage("Film", filmId));
            }
        }
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Adding like: filmId={}, userId={}", filmId, userId);
        if (userStorage.getUserById(userId).isPresent() && filmStorage.getFilmById(filmId).isPresent()) {
            filmStorage.addLike(filmId, userId);
            log.debug("Like added successfully: filmId={}", filmId);
        } else {
            if (userStorage.getUserById(userId).isEmpty()) {
                throw new NotFoundException(FormatUtil.formatNotFoundMessage("User", userId));
            }
            if (filmStorage.getFilmById(filmId).isEmpty()) {
                throw new NotFoundException(FormatUtil.formatNotFoundMessage("Film", filmId));
            }
        }
    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count)
                .stream()
                .map(this::mapToFilmDtoWithGenres)
                .toList();
    }

    public MpaDto getMpaById(Long mpaId) {
        Mpa mpa = mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        FormatUtil.formatNotFoundMessage("MPA rating", mpaId)));
        return new MpaDto(mpa.getId(), mpa.getName());
    }

    public Collection<MpaDto> getAllMpa() {
        Collection<Mpa> mpas = mpaStorage.getMpas();
        return mpas.stream()
                .map(mpa -> new MpaDto(mpa.getId(), mpa.getName()))
                .toList();
    }

    private Mpa validateAndFetchMpa(Long mpaId) {
        return mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        FormatUtil.formatNotFoundMessage("MPA rating", mpaId)));
    }

    public List<GenreDto> getAllGenres() {
        Collection<Genre> genres = genreStorage.getAllGenres();
        return genres.stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                .toList();
    }

    private List<GenreDto> getFilmGenres(Long filmId) {
        List<Genre> genres = genreStorage.getGenresByFilmId(filmId);
        return genres.stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                .toList();
    }

    public GenreDto getGenreById(Long genreId) {
        Genre genre = genreStorage.getGenreById(genreId)
                .orElseThrow(() -> new EntityNotFoundException(
                        FormatUtil.formatNotFoundMessage("Genre", genreId)));
        return new GenreDto(genre.getId(), genre.getName());
    }
}