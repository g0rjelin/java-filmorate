package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryRatingStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class FilmControllerTests {
    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmServiceImpl(new InMemoryFilmStorage(new HashMap<>()),
                new InMemoryUserStorage(new HashMap<>()),
                new InMemoryRatingStorage(),
                new InMemoryGenreStorage()));
    }

    @Test
    public void validateNullOrBlankName() {
        final Film nullNameFilm = Film.builder()
                .id(1)
                .description("null name film description")
                .duration(10)
                .mpa(Rating.builder().id(1).name("G").build())
                .releaseDate(LocalDate.now())
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(nullNameFilm),
                        "Не вызывается исключение при передаче пустого имени фильма");
        Assertions.assertEquals("Название не может быть пустым", validationException.getMessage(),
                "Неверное сообщение при валидации пустого имени фильма");

        final Film blankNameFilm = Film.builder()
                .id(1)
                .name("   ")
                .description("blank name film description")
                .duration(10)
                .mpa(Rating.builder().id(1).build())
                .releaseDate(LocalDate.now())
                .build();

        validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(blankNameFilm),
                        "Не вызывается исключение, если в имени фильма переданы только пробелы");
        Assertions.assertEquals("Название не может быть пустым", validationException.getMessage(),
                "Неверное сообщение при валидации имени фильма только с пробелами");
    }

    @Test
    public void validateNullOrBlankDescription() {
        final Film nullDescriptionFilm = Film.builder()
                .id(1)
                .name("null description film")
                .duration(10)
                .mpa(Rating.builder().id(1).build())
                .releaseDate(LocalDate.now())
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(nullDescriptionFilm),
                        "Не вызывается исключение при передаче пустого описания фильма");
        Assertions.assertEquals("Описание фильма не может быть пустым", validationException.getMessage(),
                "Неверное сообщение при валидации пустого описания фильма");

        final Film blankDescriptionFilm = Film.builder()
                .id(1)
                .name("blank film description")
                .description("   ")
                .duration(10)
                .mpa(Rating.builder().id(1).build())
                .releaseDate(LocalDate.now())
                .build();

        validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(blankDescriptionFilm),
                        "Не вызывается исключение, если в описании фильма переданы только пробелы");
        Assertions.assertEquals("Описание фильма не может быть пустым", validationException.getMessage(),
                "Неверное сообщение при валидации описания фильма только из пробелов");
    }

    @Test
    public void validateMaxFilmDescriptionLength() {
        final int maxFilmDescriptionLength = 200;
        final Film film = Film.builder()
                .id(1)
                .name("TestDescriptionName")
                .description("a".repeat(maxFilmDescriptionLength + 1))
                .duration(10)
                .mpa(Rating.builder().id(1).build())
                .releaseDate(LocalDate.now())
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                        "Не вызывается исключение при превышении максимальной длины описания фильма");
        Assertions.assertEquals(
                String.format("Превышена максимальная длина описания - %d символов", maxFilmDescriptionLength),
                validationException.getMessage(),
                "Неверное сообщение при проверке описания фильма на превышение максимальной длины описания фильма");
    }

    @Test
    public void validateNullReleaseDate() {
        final Film nullReleaseDateFilm = Film.builder()
                .id(1)
                .name("null release date film")
                .description("Test Null Release Date Description")
                .duration(10)
                .mpa(Rating.builder().id(1).build())
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(nullReleaseDateFilm),
                        "Не вызывается исключение при передаче пустой даты релиза фильма");
        Assertions.assertEquals("Дата релиза фильма не может быть пустой", validationException.getMessage(),
                "Неверное сообщение при валидации пустой даты релиза фильма");
    }

    @Test
    public void validateMinReleaseDate() {
        final LocalDate minFilmReleaseDate = LocalDate.of(1895, 12, 28);
        final Film film = Film.builder()
                .id(1)
                .name("TestReleaseDateName")
                .description("Test Release Date Description")
                .duration(10)
                .mpa(Rating.builder().id(1).build())
                .releaseDate(minFilmReleaseDate.minusDays(1))
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                        "Не вызывается исключение при указании даты создания фильма меньше минимально допустимой");
        Assertions.assertEquals(
                "Дата релиза не может быть раньше " + minFilmReleaseDate.format(DateTimeFormatter.ISO_DATE),
                validationException.getMessage(),
                "Неверное сообщение при проверке даты релиза фильма");
    }

    @Test
    public void validateNullDuration() {
        final Film film = Film.builder()
                .id(1)
                .name("TestNullDurationName")
                .description("Test Null Duration Description")
                .mpa(Rating.builder().id(1).build())
                .releaseDate(LocalDate.now())
                .build();
        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                        "Не вызывается исключение при указании пустой продолжительности фильма");
        Assertions.assertEquals("Продолжительность фильма не может быть пустой", validationException.getMessage(),
                "Неверное сообщение при валидации пустой продолжительности фильма");
    }

    @Test
    public void validateDuration() {
        final Film film = Film.builder()
                .id(1)
                .name("TestDurationName")
                .description("Test Duration Description")
                .duration(0)
                .mpa(Rating.builder().id(1).build())
                .releaseDate(LocalDate.now())
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                        "Не вызывается исключение при указании не положительной продолжительности фильма");
        Assertions.assertEquals("Продолжительность фильма должна быть положительным числом.",
                validationException.getMessage(),
                "Неверное сообщение при проверке продолжительности фильма");
    }
}


