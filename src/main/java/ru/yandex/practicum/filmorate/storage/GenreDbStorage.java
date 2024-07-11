package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreMapper;

    @Override
    public Collection<Genre> findAll() {
        String query = "SELECT * FROM genre";
        return jdbc.query(query, genreMapper);
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        String query = "SELECT id, name FROM genre WHERE id = ?";
        try {
            Genre result = jdbc.queryForObject(query, genreMapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            String idNotFound = String.format("Жанр фильма с id = %d не найден", id);
            log.warn(idNotFound);
            return Optional.empty();
        }
    }
}
