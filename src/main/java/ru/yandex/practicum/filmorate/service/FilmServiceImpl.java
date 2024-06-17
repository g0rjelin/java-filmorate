package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film findFilmById(Integer id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", id)));
    }

    @Override
    public Film create(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    @Override
    public Film update(Film updFilm) {
        return filmStorage.update(updFilm);
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        film.getUserLikes().add(userId);
        log.info("Добавлен лайк пользователя c id = {} к фильму с filmId = {}", userId, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        film.getUserLikes().remove(userId);
        log.info("Удален лайк пользователя c id = {} к фильму с filmId = {}", userId, filmId);
    }

    @Override
    public Collection<Film> getTopPopularFilms(int count) {
        Collection<Film> topPopularFilms = filmStorage.findAll().stream()
                .sorted((f1, f2) -> Long.compare(f2.getUserLikes().size(), f1.getUserLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
        log.debug("Список {} наиболее популярных фильмов для вывода: {}", count, topPopularFilms);
        return topPopularFilms;
    }
}
