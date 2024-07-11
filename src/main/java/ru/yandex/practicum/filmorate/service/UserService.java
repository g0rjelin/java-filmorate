package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAll();

    UserDto findUserById(Long id);

    UserDto create(User newUser);

    UserDto update(User updUser);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<UserDto> getUserFriends(Long userId);

    Collection<UserDto> getCommonFriends(Long userId, Long otherId);
}
