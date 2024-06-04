package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;


public class FilmControllerTests {
    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    public void validateNullOrBlankName() {
        final Film nullNameFilm = Film.builder()
                .id(1)
                .description("null name film description")
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(nullNameFilm),
                "Не вызывается исключение при передаче пустого имени фильма");

        final Film blankNameFilm = Film.builder()
                .id(1)
                .name("   ")
                .description("blank name film description")
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(blankNameFilm),
                "Не вызывается исключение, если в имени фильма переданы только пробелы");
    }

    @Test
    public void validateNullOrBlankDescription() {
        final Film nullDescriptionFilm = Film.builder()
                .id(1)
                .name("null description film")
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(nullDescriptionFilm),
                "Не вызывается исключение при передаче пустого описания фильма");

        final Film blankDescriptionFilm = Film.builder()
                .id(1)
                .name("blank film description")
                .description("   ")
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(blankDescriptionFilm),
                "Не вызывается исключение, если в описании фильма переданы только пробелы");
    }

    @Test
    public void validateMaxFilmDescriptionLength() {
        final int maxFilmDescriptionLengthPlus = 201;
        final Film film = Film.builder()
                .id(1)
                .name("TestDescriptionName")
                .description("a".repeat(maxFilmDescriptionLengthPlus))
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                "Не вызывается исключение при превышении максимальной длины описания фильма");
    }

    @Test
    public void validateNullReleaseDate() {
        final Film nullReleaseDateFilm = Film.builder()
                .id(1)
                .name("null release date film")
                .duration(10)
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(nullReleaseDateFilm),
                "Не вызывается исключение при передаче пустой даты создания фильма");
    }

    @Test
    public void validateMinReleaseDate() {
        final LocalDate preMinFilmReleaseDate = LocalDate.of(1895, 12, 28).minusDays(1);
        final Film film = Film.builder()
                .id(1)
                .name("TestReleaseDateName")
                .description("Test Release Date Description")
                .duration(10)
                .releaseDate(preMinFilmReleaseDate)
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                "Не вызывается исключение при указании даты создания фильма меньше минимально допустимой");
    }

    @Test
    public void validateNullDuration() {
        final Film film = Film.builder()
                .id(1)
                .name("TestNullDurationName")
                .description("Test Null Duration Description")
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                "Не вызывается исключение при указании пустой продолжительности фильма");
    }

    @Test
    public void validateDuration() {
        final Film film = Film.builder()
                .id(1)
                .name("TestDurationName")
                .description("Test Duration Description")
                .duration(0)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film),
                "Не вызывается исключение при указании не положительной продолжительности фильма");

    }
}


