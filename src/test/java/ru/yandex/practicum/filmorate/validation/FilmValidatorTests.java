package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;


public class FilmValidatorTests {
    FilmValidator filmValidator;

    @BeforeEach
    void setUp() {
        filmValidator = new FilmValidator();
    }

    @Test
    public void validateNullOrBlankName() {
        final Film nullNameFilm = Film.builder()
                .id(1)
                .description("null name film description")
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmValidator.validate(nullNameFilm),
                "Не вызывается исключение при передаче пустого имени фильма");

        final Film blankNameFilm = Film.builder()
                .id(1)
                .name("   ")
                .description("blank name film description")
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmValidator.validate(nullNameFilm),
                "Не вызывается исключение, если в имени фильма переданы только пробелы");
    }

    @Test
    public void validateMaxFilmDescriptionLength() {
        final Film film = Film.builder()
                .id(1)
                .name("TestDescriptionName")
                .description("a".repeat(FilmValidator.MAX_FILM_DESCRIPTION_LENGTH + 1))
                .duration(10)
                .releaseDate(LocalDate.now())
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmValidator.validate(film),
                "Не вызывается исключение при превышении максимальной длины описания фильма");
    }

    @Test
    public void validateMinReleaseDate() {
        final Film film = Film.builder()
                .id(1)
                .name("TestReleaseDateName")
                .description("Test Release Date Description")
                .duration(10)
                .releaseDate(FilmValidator.MIN_FILM_RELEASE_DATE.minusDays(1))
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmValidator.validate(film),
                "Не вызывается исключение при указании даты создания фильма меньше минимально допустимой");
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

        Assertions.assertThrows(ValidationException.class, () -> filmValidator.validate(film),
                "Не вызывается исключение при указании не положительной продолжительности фильма");

    }
}


