package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film newFilm) {
        FilmValidator.validateNull(newFilm);
        FilmValidator.validateFormat(newFilm);
        newFilm.setId((getNextId()));
        if (Objects.isNull(newFilm.getUserLikes())) {
            newFilm.setUserLikes(new HashSet<>());
        }
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film updFilm) {
        if (!films.containsKey(updFilm.getId()) || updFilm.getName().isBlank()) {
            String idNotFound = String.format("Фильм с id = %d не найден", updFilm.getId());
            log.warn(idNotFound);
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

    @Override
    public Optional<Film> findFilmById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        films.get(filmId).getUserLikes().add(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        films.get(filmId).getUserLikes().remove(userId);
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
