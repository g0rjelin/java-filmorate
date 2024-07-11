package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
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
    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::modelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.modelToDto(userStorage.findUserById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id))));
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
        userStorage.addFriend(userId, friendId);
        log.info("Пользователь c friendId = {} добавлен в список друзей пользователя с id = {}", friendId, userId);
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
        if (userStorage.deleteFriend(user.getId(), friend.getId())) {
            log.info("Пользователь c friendId = {} удален из списка друзей пользователя с id = {}", friendId, userId);
        }
    }

    @Override
    public Collection<UserDto> getUserFriends(Long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        Collection<UserDto> userFriends = user.getFriends().stream()
                .map(id -> UserMapper.modelToDto(userStorage.findUserById(id).get()))
                .collect(Collectors.toList());
        log.debug("Список друзей пользователя {} для вывода: {}", userId, userFriends);
        return userFriends;
    }

    @Override
    public Collection<UserDto> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        User other = userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", otherId)));
        Collection<UserDto> commonFriends = user.getFriends().stream()
                .filter(u -> other.getFriends().stream()
                        .anyMatch(u::equals))
                .map(id -> UserMapper.modelToDto(userStorage.findUserById(id).get()))
                .collect(Collectors.toList());
        log.debug("Список общих друзей пользователей {} и {} для вывода: {}", userId, otherId, commonFriends);
        return commonFriends;
    }

    @Override
    public UserDto create(User newUser) {
        return UserMapper.modelToDto(userStorage.create(newUser));
    }

    @Override
    public UserDto update(User updUser) {
        return UserMapper.modelToDto(userStorage.update(updUser));
    }
}
