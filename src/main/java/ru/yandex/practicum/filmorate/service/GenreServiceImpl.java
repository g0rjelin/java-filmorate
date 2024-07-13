package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public Collection<GenreDto> findAll() {
        return genreStorage.findAll().stream().map(GenreMapper::modelToDto).toList();
    }

    @Override
    public GenreDto findGenreById(Integer id) {
        return GenreMapper.modelToDto(genreStorage.findGenreById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Жанр фильма с id = %d не найден", id))));
    }
}
