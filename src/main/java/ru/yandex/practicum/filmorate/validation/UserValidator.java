package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
public class UserValidator {

    public static void validateNull(User user) {
        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank()) {
            String validationViolation = "Электронная почта не может быть пустой";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (Objects.isNull(user.getLogin()) || user.getLogin().isBlank()) {
            String validationViolation = "Логин не может быть пустым";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (Objects.isNull(user.getBirthday())) {
            String validationViolation = "Дата рождения не должна быть пустой";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
    }

    public static void validateFormat(User user) {
        if (!Objects.isNull(user.getEmail()) && !user.getEmail().contains("@")) {
            String validationViolation = "Электронная почта не должна содержать символ @";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (!Objects.isNull(user.getLogin()) && user.getLogin().contains(" ")) {
            String validationViolation = "Логин не может содержать пробелы";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (!Objects.isNull(user.getBirthday()) && user.getBirthday().isAfter(LocalDate.now())) {
            String validationViolation = "Дата рождения не может быть в будущем";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
    }
}
