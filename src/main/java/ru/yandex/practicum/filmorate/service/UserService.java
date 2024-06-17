package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> findAll();

    User findUserById(Long id);

    User create(User newUser);

    User update(User updUser);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getUserFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherId);
}
