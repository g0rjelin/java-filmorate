package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

public class InMemoryRatingStorage implements RatingStorage {
    @Override
    public Collection<Rating> findAll() {
        return null;
    }

    @Override
    public Optional<Rating> findRatingById(Integer id) {
        return Optional.ofNullable(Rating.builder().id(id).name(
                switch (id) {
                    case 1 -> "G";
                    case 2 -> "PG";
                    case 3 -> "PG-13";
                    case 4 -> "R";
                    case 5 -> "NC-17";
                    default -> throw new IllegalStateException("Unexpected value: " + id);
                }).build());

    }
}
