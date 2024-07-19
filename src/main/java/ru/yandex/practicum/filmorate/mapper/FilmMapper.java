package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public final class FilmMapper {
    public static FilmDto modelToDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .duration(film.getDuration())
                .releaseDate(film.getReleaseDate())
                .userLikes(Objects.isNull(film.getUserLikes()) ? new HashSet<>() : film.getUserLikes())
                .mpa(film.getMpa())
                .genres(film.getGenres().stream()
                        .map(GenreMapper::modelToDto)
                        .sorted(Comparator.comparingInt(GenreDto::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .build();
    }
}