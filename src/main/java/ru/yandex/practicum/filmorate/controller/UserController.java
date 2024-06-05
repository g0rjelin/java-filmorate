package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Список пользователей для вывода: {}", users.values());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        UserValidator.validateNull(newUser);
        UserValidator.validateFormat(newUser);
        newUser.setId(getNextId());
        if (Objects.isNull(newUser.getName()) || newUser.getName().isBlank()) {
            log.debug(
                    "При создании пользователя получено пусто имя Name пользователя с id = {}, в качестве имени будет использован логин {}",
                    newUser.getId(), newUser.getLogin());
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} добавлен", newUser);
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User updUser) {
        if (!users.containsKey(updUser.getId())) {
            String idNotFound = String.format("Пользователь с id = %d не найден", updUser.getId());
            log.error(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        UserValidator.validateFormat(updUser);
        User oldUser = users.get(updUser.getId());
        oldUser.setLogin(Objects.isNull(updUser.getLogin()) || updUser.getLogin().isBlank() ? oldUser.getLogin() :
                updUser.getLogin());
        oldUser.setEmail(Objects.isNull(updUser.getEmail()) || updUser.getEmail().isBlank() ? oldUser.getEmail() :
                updUser.getEmail());
        oldUser.setBirthday(Objects.isNull(updUser.getBirthday()) ? oldUser.getBirthday() : updUser.getBirthday());
        oldUser.setName(Objects.isNull(updUser.getName()) || updUser.getName().isBlank() ? oldUser.getName() :
                updUser.getName());
        log.info("Пользователь {} обновлен", updUser);
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

