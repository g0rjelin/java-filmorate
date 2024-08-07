package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;

    @Override
    public Collection<FilmDto> findAll() {
        Collection<Film> allFilms = filmStorage.findAll();
        return allFilms.stream().map(FilmMapper::modelToDto).toList();
    }

    @Override
    public FilmDto findFilmById(Integer id) {
        return FilmMapper.modelToDto(filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", id))));
    }

    @Override
    public FilmDto create(Film newFilm) {
        FilmValidator.validateNull(newFilm);
        FilmValidator.validateFormat(newFilm);
        Integer ratingId = newFilm.getMpa().getId();
        ratingStorage.findRatingById(ratingId).orElseThrow(
                () -> new ValidationException(String.format("Рейтинг фильма с id = %d не найден", ratingId)));
        Collection<Genre> genres = newFilm.getGenres();
        if (!Objects.isNull(genres)) {
            Collection<Integer> genresIdsDict =
                    genreStorage.findAll().stream().map(Genre::getId).collect(Collectors.toSet());
            for (Genre genre : genres) {
                if (!genresIdsDict.contains(genre.getId())) {
                    throw new ValidationException(String.format("Жанр фильма с id = %d не найден", genre.getId()));
                }
            }
        }
        Film film = filmStorage.create(newFilm);
        return FilmMapper.modelToDto(film);
    }

    @Override
    public FilmDto update(Film updFilm) {
        FilmValidator.validateFormat(updFilm);
        Film oldFilm = filmStorage.findFilmById(updFilm.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", updFilm.getId())));
        oldFilm.setDescription(Objects.isNull(updFilm.getDescription()) || updFilm.getDescription().isBlank() ?
                oldFilm.getDescription() : updFilm.getDescription());
        oldFilm.setName(Objects.isNull(updFilm.getName()) || updFilm.getName().isBlank() ?
                oldFilm.getName() : updFilm.getName());
        oldFilm.setReleaseDate(
                Objects.isNull(updFilm.getReleaseDate()) ? oldFilm.getReleaseDate() : updFilm.getReleaseDate());
        oldFilm.setDuration(Objects.isNull(updFilm.getDuration()) ? oldFilm.getDuration() : updFilm.getDuration());
        oldFilm.setMpa(Objects.isNull(updFilm.getMpa()) ? oldFilm.getMpa() : updFilm.getMpa());
        oldFilm.setGenres(Objects.isNull(updFilm.getGenres()) ? oldFilm.getGenres() : updFilm.getGenres());
        return FilmMapper.modelToDto(filmStorage.update(updFilm));
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        filmStorage.addLike(filmId, userId);
        log.info("Добавлен лайк пользователя c id = {} к фильму с filmId = {}", userId, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        filmStorage.deleteLike(filmId, userId);
        log.info("Удален лайк пользователя c id = {} к фильму с filmId = {}", userId, filmId);
    }

    @Override
    public Collection<FilmDto> getTopPopularFilms(int count) {
        Collection<FilmDto> topPopularFilms = filmStorage.findAll().stream()
                .map(FilmMapper::modelToDto)
                .sorted((f1, f2) -> Long.compare(f2.getUserLikes().size(), f1.getUserLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
        log.debug("Список {} наиболее популярных фильмов для вывода: {}", count, topPopularFilms);
        return topPopularFilms;
    }
}
