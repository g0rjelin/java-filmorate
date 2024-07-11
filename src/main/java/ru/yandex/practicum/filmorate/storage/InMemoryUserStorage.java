package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;

    @Override
    public Collection<User> findAll() {
        log.debug("Список пользователей для вывода: {}", users.values());
        return users.values();
    }

    @Override
    public User create(User newUser) {
        UserValidator.validateNull(newUser);
        UserValidator.validateFormat(newUser);
        newUser.setId(getNextId());
        if (Objects.isNull(newUser.getName()) || newUser.getName().isBlank()) {
            log.debug(
                    "При создании пользователя получено пусто имя Name пользователя с id = {}, в качестве имени будет использован логин {}",
                    newUser.getId(), newUser.getLogin());
            newUser.setName(newUser.getLogin());
        }
        if (Objects.isNull(newUser.getFriends())) {
            newUser.setFriends(new HashSet<>());
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} добавлен", newUser);
        return newUser;
    }

    @Override
    public User update(User updUser) {
        if (!users.containsKey(updUser.getId())) {
            String idNotFound = String.format("Пользователь с id = %d не найден", updUser.getId());
            log.warn(idNotFound);
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

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        users.get(friendId).getFriends().remove(friendId);
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        return !Objects.isNull(users.get(userId).getFriends().remove(friendId));
    }
}
