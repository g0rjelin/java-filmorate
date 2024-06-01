package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
public class FilmValidator {
    public static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public void validate(Film film) {
        if (Objects.isNull(film.getName()) || film.getName().isBlank()) {
            String validationViolation = "Название не может быть пустым";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            String validationViolation =
                    String.format("Превышена максимальная длина описания - %d символов", MAX_FILM_DESCRIPTION_LENGTH);
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (film.getReleaseDate().isBefore(MIN_FILM_RELEASE_DATE)) {
            String validationViolation = "Дата релиза не может быть раньше " + MIN_FILM_RELEASE_DATE.format(
                    DateTimeFormatter.ISO_DATE);
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (film.getDuration() <= 0) {
            String validationViolation = "Продолжительность фильма должна быть положительным числом.";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
    }
}
