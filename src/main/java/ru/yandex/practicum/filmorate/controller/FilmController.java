package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        FilmValidator.validateNull(newFilm);
        FilmValidator.validateFormat(newFilm);
        newFilm.setId((getNextId()));
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film updFilm) {
        if (!films.containsKey(updFilm.getId()) || updFilm.getName().isBlank()) {
            String idNotFound = String.format("Фильм с id = %d не найден", updFilm.getId());
            log.error(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        FilmValidator.validateFormat(updFilm);
        Film oldFilm = films.get(updFilm.getId());
        oldFilm.setDescription(Objects.isNull(updFilm.getDescription()) || updFilm.getDescription().isBlank() ?
                oldFilm.getDescription() : updFilm.getDescription());
        oldFilm.setName(Objects.isNull(updFilm.getName()) || updFilm.getName().isBlank() ?
                oldFilm.getName() : updFilm.getName());
        oldFilm.setReleaseDate(
                Objects.isNull(updFilm.getReleaseDate()) ? oldFilm.getReleaseDate() : updFilm.getReleaseDate());
        oldFilm.setDuration(Objects.isNull(updFilm.getDuration()) ? oldFilm.getDuration() : updFilm.getDuration());
        return oldFilm;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
