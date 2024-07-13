package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User create(User newUser);

    User update(User updUser);

    Optional<User> findUserById(Long id);

    void addFriend(Long userId, Long friendId);

    boolean deleteFriend(Long userId, Long friendId);

    Set<User> getFriendsByUserId(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherId);

}
