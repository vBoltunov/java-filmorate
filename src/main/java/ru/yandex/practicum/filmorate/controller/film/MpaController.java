package ru.yandex.practicum.filmorate.controller.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.MpaDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<MpaDto> getAllMpa() {
        log.info("Fetching all MPAs.");
        return filmService.getAllMpa();
    }

    @GetMapping("/{mpa-id}")
    @ResponseStatus(HttpStatus.OK)
    public MpaDto getMpaById(@PathVariable("mpa-id") Long mpaId) {
        log.info("Fetching MPA by id: {}", mpaId);
        return filmService.getMpaById(mpaId);
    }
}