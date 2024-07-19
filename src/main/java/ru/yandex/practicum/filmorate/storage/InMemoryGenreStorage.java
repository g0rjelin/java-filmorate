package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public class InMemoryGenreStorage implements GenreStorage {
    @Override
    public Collection<Genre> findAll() {
        return null;
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        return Optional.empty();
    }
}
