package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final RatingRowMapper ratingMapper;
    private final GenreStorage genreStorage;

    private Set<Genre> findFilmGenresIdsByFilmId(Integer filmId) {
        try {
            Set<Integer> genreIds = new HashSet<>(jdbc.queryForList("SELECT fg.genre_id FROM film_genre fg " +
                    "WHERE fg.film_id = ? ", Integer.class, filmId));
            Set<Genre> genres = genreIds.stream()
                    .map(genreId -> genreStorage.findGenreById(genreId).get())
                    .collect(Collectors.toSet());
            return genres;
        } catch (EmptyResultDataAccessException ignored) {
            String idsNotFound = String.format("Жанры фильма с filmId = %d не найдены", filmId);
            log.warn(idsNotFound);
            return new HashSet<>();
        }
    }

    private Set<Long> findFilmLikesByFilmId(Integer filmId) {
        try {
            return new HashSet<>(jdbc.queryForList("SELECT fu.user_id FROM film_userlikes fu " +
                    "WHERE fu.film_id = ? ", Long.class, filmId));
        } catch (EmptyResultDataAccessException ignored) {
            String idsNotFound = String.format("Лайки фильма с filmId = %d не найдены", filmId);
            log.warn(idsNotFound);
            return new HashSet<>();
        }
    }

    @Override
    public Collection<Film> findAll() {
        String query =
                "SELECT f.id, f.name, f.duration, f.rating_id, r.name as rating_name, f.description, f.release_date " +
                        "FROM film f " +
                        "JOIN rating r ON f.rating_id = r.id";
        Collection<Film> allFilms = jdbc.query(query, mapper);
        allFilms.forEach(film -> {
            Set<Genre> genres = new HashSet<>(findFilmGenresIdsByFilmId(film.getId()));
            Set<Long> userLikes = findFilmLikesByFilmId(film.getId());
            film.setGenres(genres);
            film.setUserLikes(userLikes);
        });
        return allFilms;
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        String query = "INSERT INTO film_userlikes(film_id, user_id) " +
                "VALUES (?, ? )";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, filmId);
            ps.setObject(2, userId);
            return ps;
        }, keyHolder);
        Long filmUserLikeId = keyHolder.getKeyAs(Long.class);
        if (filmUserLikeId != null) {
            log.info("Лайк к фильму с id = {} от пользователя с id = {} добавлен", filmId, userId);
        } else {
            String errSave = "Не удалось сохранить данные";
            log.warn(errSave);
            throw new RuntimeException(errSave);
        }
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        String query = "DELETE FROM film_userlikes WHERE film_id = ? AND user_id = ? ";
        int rowsDeleted = jdbc.update(query, filmId, userId);
        if (rowsDeleted > 0) {
            log.info("Лайк к фильму с id = {} от пользователя с id = {} удален", filmId, userId);
        }
    }

    @Override
    public Film create(Film newFilm) {
        FilmValidator.validateNull(newFilm);
        FilmValidator.validateFormat(newFilm);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO film(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ? )";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newFilm.getName());
            ps.setObject(2, newFilm.getDescription());
            ps.setObject(3, newFilm.getReleaseDate());
            ps.setObject(4, newFilm.getDuration());
            ps.setObject(5, newFilm.getMpa().getId());
            return ps;
        }, keyHolder);
        Integer filmId = keyHolder.getKeyAs(Integer.class);
        String genreQuery = "INSERT INTO film_genre(film_id, genre_id) " +
                "VALUES (?, ?)";
        if (!Objects.isNull(newFilm.getGenres())) {
            updateFilmGenre(filmId, newFilm.getGenres());
        }
        if (filmId != null) {
            Film film = findFilmById(filmId).get();
            log.info("Фильм {} добавлен", film);
            return film;
        } else {
            String errSave = "Не удалось сохранить данные";
            log.warn(errSave);
            throw new RuntimeException(errSave);
        }
    }

    private void updateFilmGenre(Integer filmId, Collection<Genre> genres) {
        String deleteGenreQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbc.update(deleteGenreQuery, filmId);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String genreQuery = "INSERT INTO film_genre(film_id, genre_id) " +
                "VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbc.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(genreQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, filmId);
                ps.setObject(2, genre.getId());
                return ps;
            }, keyHolder);
        }
    }

    @Override
    public Film update(Film updFilm) {
        String query = "UPDATE film " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id =? " +
                "WHERE id = ?";
        FilmValidator.validateFormat(updFilm);
        Optional<Film> optFilm = findFilmById(updFilm.getId());
        if (optFilm.isEmpty()) {
            String idNotFound = String.format("Фильм с id = %d не найден", updFilm.getId());
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        Film oldFilm = optFilm.get();
        oldFilm.setDescription(Objects.isNull(updFilm.getDescription()) || updFilm.getDescription().isBlank() ?
                oldFilm.getDescription() : updFilm.getDescription());
        oldFilm.setName(Objects.isNull(updFilm.getName()) || updFilm.getName().isBlank() ?
                oldFilm.getName() : updFilm.getName());
        oldFilm.setReleaseDate(
                Objects.isNull(updFilm.getReleaseDate()) ? oldFilm.getReleaseDate() : updFilm.getReleaseDate());
        oldFilm.setDuration(Objects.isNull(updFilm.getDuration()) ? oldFilm.getDuration() : updFilm.getDuration());
        oldFilm.setMpa(Objects.isNull(updFilm.getMpa()) ? oldFilm.getMpa() : updFilm.getMpa());
        oldFilm.setGenres(Objects.isNull(updFilm.getGenres()) ? oldFilm.getGenres() : updFilm.getGenres());
        int rowsUpdated = jdbc.update(query,
                oldFilm.getName(),
                oldFilm.getDescription(),
                oldFilm.getReleaseDate(),
                oldFilm.getDuration(),
                oldFilm.getMpa().getId(),
                oldFilm.getId());
        updateFilmGenre(oldFilm.getId(), oldFilm.getGenres());
        if (rowsUpdated == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        } else {
            log.info("Фильм с id = {} обновлен", updFilm.getId());
            return findFilmById(oldFilm.getId()).get();
        }
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        String query =
                "SELECT f.id, f.name, f.duration, f.rating_id, r.name as rating_name, f.description, f.release_date " +
                        "FROM film f " +
                        "JOIN rating r ON f.rating_id = r.id " +
                        "WHERE f.id = ? ";
        try {
            Film result = jdbc.queryForObject(query, mapper, id);
            Set<Integer> genreIds = new HashSet<>(jdbc.queryForList("SELECT fg.genre_id FROM film_genre fg " +
                    "WHERE fg.film_id = ?", Integer.class, id));
            Set<Genre> genres = findFilmGenresIdsByFilmId(id);
            result.setGenres(genres);
            Set<Long> userLikes = findFilmLikesByFilmId(id);
            result.setUserLikes(userLikes);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }

    }
}
