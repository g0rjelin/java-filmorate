package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidatorTests {
    UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    public void validateEmail() {
        final User nullEmailUser = User.builder()
                .id(1)
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .login("myLogin")
                .build();

        Assertions.assertThrows(ValidationException.class, () -> userValidator.validate(nullEmailUser),
                "Не вызывается исключение при передаче пустой электронной почты");

        final User noAtEmailUser = User.builder()
                .id(1)
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .email("some#email.com")
                .login("myLogin")
                .build();

        Assertions.assertThrows(ValidationException.class, () -> userValidator.validate(noAtEmailUser),
                "Не вызывается исключение при передаче электронной почты без символа @");
    }

    @Test
    public void validateLogin() {
        final User nullLoginUser = User.builder()
                .id(1)
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .email("some@email.com")
                .build();

        Assertions.assertThrows(ValidationException.class, () -> userValidator.validate(nullLoginUser),
                "Не вызывается исключение при передаче пустого логина");

        final User loginWithSpaceUser = User.builder()
                .id(1)
                .login("My Login")
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .email("some@email.com")
                .build();

        Assertions.assertThrows(ValidationException.class, () -> userValidator.validate(loginWithSpaceUser),
                "Не вызывается исключение при передаче логина, содержащего пробел");
    }

    @Test
    public void validateBirthday() {
        final User user = User.builder()
                .id(1)
                .name("My Name")
                .birthday(LocalDate.now().plusDays(1))
                .email("some@email.com")
                .login("myLogin")
                .build();

        Assertions.assertThrows(ValidationException.class, () -> userValidator.validate(user),
                "Не вызывается исключение при передаче дня рождения в будущем");
    }

    @Test
    public void validateNullName() {
        final User user = User.builder()
                .id(1)
                .birthday(LocalDate.of(2020, 1, 1))
                .email("some@email.com")
                .login("myLogin")
                .build();

        Assertions.assertDoesNotThrow(() -> userValidator.validate(user),
                "Вызывается исключение при передаче пустого имени");
    }
}