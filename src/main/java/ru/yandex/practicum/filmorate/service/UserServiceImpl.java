package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
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
        return userStorage.findUserById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        User friend = userStorage.findUserById(friendId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь для добавления в друзья с id = %d не найден", friendId)));
        if (userId.equals(friendId)) {
            String equalIds = String.format(
                    "Пользователь не может добавить себя в друзья. Для id пользователя и id друга передано одинаковое значение %d",
                    userId);
            log.warn(equalIds);
            throw new ValidationException(equalIds);
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь c friendId = {} добавлен в список друзей пользователя с id = {}", friendId, userId);
        log.info("Пользователь c friendId = {} добавлен в список друзей пользователя с id = {}", userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        User friend = userStorage.findUserById(friendId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь для удаление из друзей с id = %d не найден", friendId)));
        if (userId.equals(friendId)) {
            String equalIds = String.format(
                    "Пользователь не может добавить себя в друзья. Для id пользователя и id друга передано одинаковое значение %d",
                    userId);
            log.warn(equalIds);
            throw new ValidationException(equalIds);
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь c friendId = {} удален из списка друзей пользователя с id = {}", friendId, userId);
        log.info("Пользователь c friendId = {} удален из списка друзей пользователя с id = {}", userId, friendId);
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        Collection<User> userFriends = user.getFriends().stream()
                .map(id -> userStorage.findUserById(id).get())
                .collect(Collectors.toList());
        log.debug("Список друзей пользователя {} для вывода: {}", userId, userFriends);
        return userFriends;
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        User other = userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", otherId)));
        Collection<User> commonFriends = user.getFriends().stream()
                .filter(u -> other.getFriends().stream()
                        .anyMatch(u::equals))
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
