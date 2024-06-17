package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Collection<Film> findAll();

    Film findFilmById(Integer id);

    Film create(Film newFilm);

    Film update(Film updFilm);

    void addLike(Integer filmId, Long userId);

    void deleteLike(Integer filmId, Long userId);

    Collection<Film> getTopPopularFilms(int count);
}
