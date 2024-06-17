package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashMap;

public class UserControllerTests {
    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserServiceImpl(new InMemoryUserStorage(new HashMap<>())));
    }

    @Test
    public void validateEmail() {
        final User nullEmailUser = User.builder()
                .id(1)
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .login("myLogin")
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> userController.create(nullEmailUser),
                        "Не вызывается исключение при передаче пустой электронной почты");
        Assertions.assertEquals("Электронная почта не может быть пустой", validationException.getMessage(),
                "Неверное сообщение при валидации пустой электронной почты");

        final User noAtEmailUser = User.builder()
                .id(1)
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .email("some#email.com")
                .login("myLogin")
                .build();

        validationException =
                Assertions.assertThrows(ValidationException.class, () -> userController.create(noAtEmailUser),
                        "Не вызывается исключение при передаче электронной почты без символа @");
        Assertions.assertEquals("Электронная почта не должна содержать символ @", validationException.getMessage(),
                "Неверное сообщение при проверке электронной почты на наличие @");
    }

    @Test
    public void validateLogin() {
        final User nullLoginUser = User.builder()
                .id(1)
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .email("some@email.com")
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> userController.create(nullLoginUser),
                        "Не вызывается исключение при передаче пустого логина");
        Assertions.assertEquals("Логин не может быть пустым", validationException.getMessage(),
                "Неверное сообщение при валидации пустого логина");

        final User loginWithSpaceUser = User.builder()
                .id(1)
                .login("My Login")
                .name("My Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .email("some@email.com")
                .build();

        validationException =
                Assertions.assertThrows(ValidationException.class, () -> userController.create(loginWithSpaceUser),
                        "Не вызывается исключение при передаче логина, содержащего пробел");
        Assertions.assertEquals("Логин не может содержать пробелы", validationException.getMessage(),
                "Неверное сообщение при проверке логина на наличие пробелов");
    }

    @Test
    public void validateNullBirthday() {
        final User user = User.builder()
                .id(1)
                .name("My Name")
                .email("some@email.com")
                .login("myLogin")
                .build();

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> userController.create(user),
                        "Не вызывается исключение при передаче пустой даты рождения");
        Assertions.assertEquals("Дата рождения не должна быть пустой", validationException.getMessage(),
                "Неверное сообщение при валидации пустой даты рождения");
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

        Exception validationException =
                Assertions.assertThrows(ValidationException.class, () -> userController.create(user),
                        "Не вызывается исключение при передаче дня рождения в будущем");
        Assertions.assertEquals("Дата рождения не может быть в будущем", validationException.getMessage(),
                "Неверное сообщение при проверке даты рождения");
    }

    @Test
    public void validateNullName() {
        final User user = User.builder()
                .id(1)
                .birthday(LocalDate.of(2020, 1, 1))
                .email("some@email.com")
                .login("myLogin")
                .build();

        Assertions.assertDoesNotThrow(() -> userController.create(user),
                "Вызывается исключение при передаче пустого имени");
    }
}