package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final FilmExtractor filmExtractor;

    private Set<Genre> findFilmGenresIdsByFilmId(Integer filmId) {
        return new HashSet<>(jdbc.query("SELECT g.* FROM film_genre fg, genre g " +
                "WHERE fg.genre_id = g.id AND fg.film_id = ? ", genreRowMapper, filmId));
    }

    private Set<Long> findFilmLikesByFilmId(Integer filmId) {
        return new HashSet<>(jdbc.queryForList("SELECT fu.user_id FROM film_userlikes fu " +
                "WHERE fu.film_id = ? ", Long.class, filmId));
    }

    @Override
    public Collection<Film> findAll() {
        String query =
                "SELECT f.*, r.name as rating_name, fg.genre_id, g.name as genre_name, fu.user_id FROM film f " +
                        "JOIN rating r ON f.rating_id = r.id " +
                        "JOIN film_genre fg ON fg.film_id = f.id " +
                        "JOIN genre g ON g.id = fg.genre_id " +
                        "LEFT JOIN film_userlikes fu ON f.id = fu.film_id ";
        return jdbc.query(query, filmExtractor);
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        String sqlQuery = "MERGE INTO film_userlikes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
        int rowsMerged = jdbc.update(sqlQuery, filmId, userId);
        if (rowsMerged > 0) {
            log.info("Лайк к фильму с id = {} от пользователя с id = {} добавлен", filmId, userId);
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
        updateFilmGenre(filmId, newFilm.getGenres());
        Film film = findFilmById(filmId).get();
        log.info("Фильм {} добавлен", film);
        return film;
    }

    private void updateFilmGenre(Integer filmId, Collection<Genre> genres) {
        if (!Objects.isNull(genres)) {
            List<Genre> genresList = genres.stream().toList();
            String genreQuery = "MERGE INTO film_genre(film_id, genre_id) KEY(film_id, genre_id) VALUES (?, ?)";
            jdbc.batchUpdate(genreQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genresList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
    }

    @Override
    public Film update(Film updFilm) {
        String query = "UPDATE film " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id =? " +
                "WHERE id = ?";
        int rowsUpdated = jdbc.update(query,
                updFilm.getName(),
                updFilm.getDescription(),
                updFilm.getReleaseDate(),
                updFilm.getDuration(),
                updFilm.getMpa().getId(),
                updFilm.getId());
        if (!Objects.isNull(updFilm.getGenres())) {
            updateFilmGenre(updFilm.getId(), updFilm.getGenres());
        }
        if (rowsUpdated == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        } else {
            log.info("Фильм с id = {} обновлен", updFilm.getId());
            return findFilmById(updFilm.getId()).get();
        }
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        String query =
                "SELECT f.*, r.name as rating_name FROM film f, rating r WHERE f.rating_id = r.id AND f.id = ? ";
        List<Film> result = jdbc.query(query, filmRowMapper, id);
        if (result.size() == 0) {
            return Optional.empty();
        }
        Film film = result.getFirst();
        Set<Genre> genres = findFilmGenresIdsByFilmId(id);
        film.setGenres(genres);
        Set<Long> userLikes = findFilmLikesByFilmId(id);
        film.setUserLikes(userLikes);
        return Optional.of(film);
    }

}
