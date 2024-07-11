package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
//@Qualifier("userDbStorage")
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM users";
        Collection<User> allUsers = jdbc.query(query, mapper);
        allUsers.forEach(user -> {
            Set<Long> friends = new HashSet<>(getFriendsByUserId(user.getId()));
            user.setFriends(friends);
        });
        return allUsers;
    }

    private Set<Long> getFriendsByUserId(Long userId) {
        String query = "SELECT uf.user_friend_id FROM user_friends uf " +
                "WHERE uf.user_id = ?";
        try {
            return new HashSet<>(jdbc.queryForList(query, Long.class, userId));
        } catch (EmptyResultDataAccessException ignored) {
            return new HashSet<>();
        }
    }

    @Override
    public User create(User newUser) {
        UserValidator.validateNull(newUser);
        UserValidator.validateFormat(newUser);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO users(email, login, name, birthday)" +
                "VALUES (?, ?, ?, ?)";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newUser.getEmail());
            ps.setObject(2, newUser.getLogin());
            ps.setObject(3, newUser.getName());
            ps.setObject(4, newUser.getBirthday());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            log.info("Пользователь {} добавлен", newUser);
            return User.builder()
                    .id(id)
                    .email(newUser.getEmail())
                    .login(newUser.getLogin())
                    .name(newUser.getName())
                    .birthday(newUser.getBirthday())
                    .build();
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    @Override
    public User update(User updUser) {
        String query = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";
        UserValidator.validateFormat(updUser);
        Optional<User> optUser = findUserById(updUser.getId());
        if (optUser.isEmpty()) {
            String idNotFound = String.format("Пользователь с id = %d не найден", updUser.getId());
            log.warn(idNotFound);
            throw new NotFoundException(idNotFound);
        }
        User oldUser = optUser.get();
        oldUser.setLogin(Objects.isNull(updUser.getLogin()) || updUser.getLogin().isBlank() ? oldUser.getLogin() :
                updUser.getLogin());
        oldUser.setEmail(Objects.isNull(updUser.getEmail()) || updUser.getEmail().isBlank() ? oldUser.getEmail() :
                updUser.getEmail());
        oldUser.setBirthday(Objects.isNull(updUser.getBirthday()) ? oldUser.getBirthday() : updUser.getBirthday());
        oldUser.setName(Objects.isNull(updUser.getName()) || updUser.getName().isBlank() ? oldUser.getName() :
                updUser.getName());
        int rowsUpdated = jdbc.update(query,
                oldUser.getEmail(),
                oldUser.getLogin(),
                oldUser.getName(),
                oldUser.getBirthday(),
                oldUser.getId());
        if (rowsUpdated == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        } else {
            log.info("Пользователь с id = {} обновлен", updUser.getId());
            return oldUser;
        }
    }


    @Override
    public Optional<User> findUserById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try {
            User result = jdbc.queryForObject(query, mapper, id);
            Set<Long> friends = new HashSet<>(getFriendsByUserId(result.getId()));
            result.setFriends(friends);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (getFriendsByUserId(userId).contains(friendId)) {
            String friendshipExists =
                    String.format("Пользователь с id = %d уже является другом пользователя c id = %d", userId,
                            friendId);
            log.warn(friendshipExists);
            throw new ValidationException(friendshipExists);
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO user_friends(user_id, user_friend_id) " +
                "VALUES ( ? , ? )";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, userId);
            ps.setObject(2, friendId);
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            log.info("Пользователь с Id = {} стал другом пользователя с Id = {}", userId,
                    friendId);
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String query = "DELETE FROM user_friends WHERE user_id = ? AND user_friend_id = ?";
        int rowsDeleted = jdbc.update(query, userId, friendId);
        return (rowsDeleted > 0);
    }
}
