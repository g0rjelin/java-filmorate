package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@UtilityClass
public class RatingMapper {
    public static RatingDto modelToDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .name(rating.getName())
                .build();
    }
}
