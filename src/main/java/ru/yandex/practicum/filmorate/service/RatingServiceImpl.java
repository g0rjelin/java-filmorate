package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingStorage ratingStorage;

    @Override
    public Collection<RatingDto> findAll() {
        return ratingStorage.findAll().stream().map(RatingMapper::modelToDto).toList();
    }

    @Override
    public RatingDto findRatingById(Integer id) {
        return RatingMapper.modelToDto(ratingStorage.findRatingById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Рейтинг фильма с id = %d не найден", id))));
    }
}
