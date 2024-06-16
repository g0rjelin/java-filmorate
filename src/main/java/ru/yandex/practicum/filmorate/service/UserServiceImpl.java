package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User findUserById(Long id) {
        Optional<User> optUser = userStorage.findUserById(id);
        if (optUser.isEmpty()) {
            String idNotFound = String.format("Пользователь с id = %d не найден", id);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        return optUser.get();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Optional<User> optUser = userStorage.findUserById(userId);
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optUser.isEmpty()) {
            String idNotFound = String.format("Пользователь с id = %d не найден", userId);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        if (optFriend.isEmpty()) {
            String idNotFound = String.format("Пользователь для добавления в друзья с id = %d не найден", friendId);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        if (userId.equals(friendId)) {
            String equalIds = String.format(
                    "Пользователь не может добавить себя в друзья. Для id пользователя и id друга передано одинаковое значение %d",
                    userId);
            log.warn(equalIds);
            throw new ValidationException(equalIds);
        }
        optUser.get().getFriends().add(friendId);
        optFriend.get().getFriends().add(userId);
        log.info("Пользователь c friendId = {} добавлен в список друзей пользователя с id = {}", friendId, userId);
        log.info("Пользователь c friendId = {} добавлен в список друзей пользователя с id = {}", userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Optional<User> optUser = userStorage.findUserById(userId);
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optUser.isEmpty()) {
            String idNotFound = String.format("Пользователь с id = %d не найден", userId);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        if (optFriend.isEmpty()) {
            String idNotFound = String.format("Пользователь для удаление из друзей с id = %d не найден", friendId);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        if (userId.equals(friendId)) {
            String equalIds = String.format(
                    "Пользователь не может добавить себя в друзья. Для id пользователя и id друга передано одинаковое значение %d",
                    userId);
            log.warn(equalIds);
            throw new ValidationException(equalIds);
        }
        optUser.get().getFriends().remove(friendId);
        optFriend.get().getFriends().remove(userId);
        log.info("Пользователь c friendId = {} удален из списка друзей пользователя с id = {}", friendId, userId);
        log.info("Пользователь c friendId = {} удален из списка друзей пользователя с id = {}", userId, friendId);
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isEmpty()) {
            String idNotFound = String.format("Пользователь с id = %d не найден", userId);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        Collection<User> userFriends = optUser.get().getFriends().stream()
                .map(id -> userStorage.findUserById(id).get())
                .collect(Collectors.toList());
        log.debug("Список друзей пользователя {} для вывода: {}", userId, userFriends);
        return userFriends;
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        Optional<User> optUser = userStorage.findUserById(userId);
        Optional<User> optOther = userStorage.findUserById(otherId);
        if (optUser.isEmpty()) {
            String idNotFound = String.format("Пользователь с id = %d не найден", userId);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        if (optOther.isEmpty()) {
            String idNotFound = String.format("Пользователь с id = %d не найден", otherId);
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        Collection<User> commonFriends = optUser.get().getFriends().stream()
                .filter(user -> optOther.get().getFriends().stream()
                        .anyMatch(user::equals))
                .map(id -> userStorage.findUserById(id).get())
                .collect(Collectors.toList());
        log.debug("Список общих друзей пользователей {} и {} для вывода: {}", userId, otherId, commonFriends);
        return commonFriends;
    }

    @Override
    public User create(User newUser) {
        return userStorage.create(newUser);
    }

    @Override
    public User update(User updUser) {
        return userStorage.update(updUser);
    }
}
