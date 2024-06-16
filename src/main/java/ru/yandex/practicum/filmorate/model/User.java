package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * User.
 */
@Data
@Builder
public class User {
    private long id;

    private String email;

    private String login;

    private String name;
    private LocalDate birthday;

    private Set<Long> friends;
}
