package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class UserExtractor implements ResultSetExtractor<Collection<User>> {
    @Override
    public Collection<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, User> data = new LinkedHashMap<>();
        while (rs.next()) {
            long userId = rs.getLong("id");
            if (data.containsKey(userId)) {
                User user = data.get(userId);
                if (rs.getLong("user_friend_id") != 0) {
                    Set<Long> friends = user.getFriends();
                    if (Objects.isNull(friends)) {
                        friends = new HashSet<>();
                    }
                    friends.add(rs.getLong("user_friend_id"));
                }
            } else {
                User user = User.builder()
                        .id(rs.getLong("id"))
                        .login(rs.getString("login"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .build();
                if (rs.getLong("user_friend_id") != 0) {
                    Set<Long> friends = new HashSet<>();
                    friends.add(rs.getLong("user_friend_id"));
                    user.setFriends(friends);
                }
                data.put(userId, user);
            }
        }
        return data.values();
    }
}
