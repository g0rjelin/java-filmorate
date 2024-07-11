package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class RatingDto {
    private final int id;
    private final String name;
}
