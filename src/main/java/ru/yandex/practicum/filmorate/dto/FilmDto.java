package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class FilmDto {
    private int id;

    private String name;

    private String description;
    private LocalDate releaseDate;
    private Integer duration; //минуты

    private Set<Long> userLikes;
    private Rating mpa;
    private Set<GenreDto> genres;
}
