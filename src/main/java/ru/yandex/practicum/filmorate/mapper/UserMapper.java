package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Objects;

@UtilityClass
public final class UserMapper {
    public static UserDto modelToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .friends(Objects.isNull(user.getFriends()) ? new HashSet<>() : user.getFriends())
                .build();
    }
}
