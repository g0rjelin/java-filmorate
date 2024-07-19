package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findFilmById(@PathVariable Integer id) {
        return filmService.findFilmById(id);
    }

    @PostMapping
    public FilmDto create(@RequestBody Film newFilm) {
        return filmService.create(newFilm);
    }

    @PutMapping
    public FilmDto update(@RequestBody Film updFilm) {
        return filmService.update(updFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> topPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        return filmService.getTopPopularFilms(count);
    }

    @GetMapping("/common")
    public Collection<FilmDto> commonFilms(
            @RequestParam() long userId, @RequestParam() long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

}
