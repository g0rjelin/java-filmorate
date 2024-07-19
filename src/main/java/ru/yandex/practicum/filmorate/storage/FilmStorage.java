package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film newFilm);

    Film update(Film updFilm);

    Optional<Film> findFilmById(Integer id);

    void addLike(Integer filmId, Long userId);

    void deleteLike(Integer filmId, Long userId);

}
