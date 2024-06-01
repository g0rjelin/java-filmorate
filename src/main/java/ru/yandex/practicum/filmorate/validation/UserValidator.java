package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
public class UserValidator {

    public void validate(User user) {
        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String validationViolation = "Электронная почта не может быть пустой и должна содержать символ @";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (Objects.isNull(user.getLogin()) || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String validationViolation = "Логин не может быть пустым и содержать пробелы";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String validationViolation = "Дата рождения не может быть в будущем";
            log.error(validationViolation);
            throw new ValidationException(validationViolation);
        }
    }
}
