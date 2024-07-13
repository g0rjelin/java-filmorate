package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserStorage.class, UserDbStorage.class, UserRowMapper.class, UserService.class,
        FilmStorage.class, FilmDbStorage.class, FilmRowMapper.class, FilmService.class, RatingRowMapper.class,
        GenreStorage.class, GenreDbStorage.class, GenreRowMapper.class, RatingDbStorage.class, RatingRowMapper.class,
        FilmExtractor.class, UserExtractor.class})
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final RatingDbStorage ratingDbStorage;

    @Test
    public void testFindUserById() {

        User user = userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());

        Optional<User> userOptional = userStorage.findUserById(user.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(eachUser ->
                        assertThat(eachUser).hasFieldOrPropertyWithValue("id", user.getId())
                );
    }

    @Test
    public void testFindAllUsers() {
        userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());

        userStorage.create(User.builder()
                .login("login2")
                .name("name 2")
                .email("some2@email.com")
                .birthday(LocalDate.now())
                .build());

        Collection<User> users = userStorage.findAll();

        Assertions.assertEquals(2, users.size());
    }

    @Test
    public void testCreateUser() {
        User user = userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());

        assertThat(user).hasFieldOrPropertyWithValue("email", "some1@email.com");

    }

    @Test
    public void testUpdateUser() {
        User user = userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());
        user = userStorage.update(User.builder()
                .id(user.getId())
                .login("login1")
                .email("some1@email.com")
                .name("Updated name")
                .birthday(LocalDate.now())
                .build());
        assertThat(user).hasFieldOrPropertyWithValue("name", "Updated name");
    }

    @Test
    public void testGetFriendsByUserId() {
        User user = userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());
        User userFriend1 = userStorage.create(User.builder()
                .login("login2")
                .name("name 2")
                .email("some2@email.com")
                .birthday(LocalDate.now())
                .build());
        User userFriend2 = userStorage.create(User.builder()
                .login("login3")
                .name("name 3")
                .email("some3@email.com")
                .birthday(LocalDate.now())
                .build());
        User userNotFriend = userStorage.create(User.builder()
                .login("login4")
                .name("name 4")
                .email("some4@email.com")
                .birthday(LocalDate.now())
                .build());

        userStorage.addFriend(user.getId(), userFriend1.getId());
        userStorage.addFriend(user.getId(), userFriend2.getId());
        Assertions.assertEquals(2, userStorage.getFriendsByUserId(user.getId()).size());
        Assertions.assertTrue(
                userStorage.getFriendsByUserId(user.getId()).stream().map(User::getId).collect(Collectors.toList())
                        .contains(userFriend1.getId()));
        Assertions.assertTrue(
                userStorage.getFriendsByUserId(user.getId()).stream().map(User::getId).collect(Collectors.toList())
                        .contains(userFriend2.getId()));
        Assertions.assertFalse(
                userStorage.getFriendsByUserId(user.getId()).stream().map(User::getId).collect(Collectors.toList())
                        .contains(userNotFriend.getId()));
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());
        User user2 = userStorage.create(User.builder()
                .login("login2")
                .name("name 2")
                .email("some2@email.com")
                .birthday(LocalDate.now())
                .build());
        User userFriend1 = userStorage.create(User.builder()
                .login("login3")
                .name("name 3")
                .email("some3@email.com")
                .birthday(LocalDate.now())
                .build());
        User userFriend2 = userStorage.create(User.builder()
                .login("login4")
                .name("name 4")
                .email("some4@email.com")
                .birthday(LocalDate.now())
                .build());
        User userCommonFriend = userStorage.create(User.builder()
                .login("login5")
                .name("name 5")
                .email("some5@email.com")
                .birthday(LocalDate.now())
                .build());

        userStorage.addFriend(user1.getId(), userFriend1.getId());
        userStorage.addFriend(user1.getId(), userCommonFriend.getId());
        userStorage.addFriend(user2.getId(), userFriend2.getId());
        userStorage.addFriend(user2.getId(), userCommonFriend.getId());
        Collection<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        Assertions.assertEquals(1, commonFriends.size());
        Assertions.assertTrue(
                commonFriends.stream().map(User::getId).collect(Collectors.toList())
                        .contains(userCommonFriend.getId()));
    }


    @Test
    public void testAddLike() {
        Film film = filmDbStorage.create(Film.builder()
                .name("Name")
                .description("Description")
                .duration(160)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Rating.builder().id(1).build())
                .build());
        User user = userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());
        filmDbStorage.addLike(film.getId(), user.getId());
        film = filmDbStorage.findFilmById(1).get();

        Assertions.assertTrue(film.getUserLikes().contains(user.getId()));
    }

    @Test
    public void testDeleteLike() {
        User user = userStorage.create(User.builder()
                .login("login1")
                .name("name 1")
                .email("some1@email.com")
                .birthday(LocalDate.now())
                .build());
        Film film = filmDbStorage.create(Film.builder()
                .name("Name")
                .description("Description")
                .duration(160)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Rating.builder().id(1).build())
                .userLikes(Set.of(user.getId()))
                .build());
        filmDbStorage.deleteLike(film.getId(), user.getId());
        Assertions.assertEquals(0, film.getUserLikes().size());

    }

    @Test
    public void testFindAllFilms() {
        filmDbStorage.create(Film.builder()
                .name("Name1")
                .description("Description1")
                .duration(160)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Rating.builder().id(1).build())
                .genres(Set.of(Genre.builder().id(1).build()))
                .build());
        filmDbStorage.create(Film.builder()
                .name("Name1")
                .description("Description1")
                .duration(160)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Rating.builder().id(1).build())
                .genres(Set.of(Genre.builder().id(1).build()))
                .build());

        Collection<Film> films = filmDbStorage.findAll();

        Assertions.assertEquals(2, films.size());
    }

    @Test
    public void testCreateFilm() {
        Film film = filmDbStorage.create(Film.builder()
                .name("Name1")
                .description("Description1")
                .duration(160)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Rating.builder().id(1).build())
                .build());
        assertThat(film).hasFieldOrPropertyWithValue("description", "Description1");

    }

    @Test
    public void testUpdateFilm() {
        Film film = filmDbStorage.create(Film.builder()
                .name("Name1")
                .description("Description1")
                .duration(160)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Rating.builder().id(1).build())
                .genres(Set.of(Genre.builder().id(1).build()))
                .build());
        film = filmDbStorage.update(Film.builder()
                .id(film.getId())
                .name("Name1")
                .mpa(film.getMpa())
                .releaseDate(film.getReleaseDate())
                .description("Updated Description1")
                .duration(film.getDuration())
                .build());
        assertThat(film).hasFieldOrPropertyWithValue("description", "Updated Description1");

    }

    @Test
    public void testFindFilmById() {

        Film film = filmDbStorage.create(Film.builder()
                .name("Name1")
                .description("Description1")
                .duration(160)
                .releaseDate(LocalDate.of(2000, 01, 01))
                .mpa(Rating.builder().id(1).build())
                .genres(Set.of(Genre.builder().id(1).build()))
                .build());

        Optional<Film> filmOptional = filmDbStorage.findFilmById(film.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(eachFilm ->
                        assertThat(eachFilm).hasFieldOrPropertyWithValue("id", film.getId())
                );
    }

    @Test
    public void testFindAllGenre() {
        Collection<Genre> genres = genreDbStorage.findAll();

        Assertions.assertEquals(6, genres.size());
    }

    @Test
    public void testFindGenreById() {
        Optional<Genre> genreOptional = genreDbStorage.findGenreById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(eachGenre ->
                        assertThat(eachGenre).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void testFindAllRating() {
        Collection<Rating> ratings = ratingDbStorage.findAll();

        Assertions.assertEquals(5, ratings.size());
    }

    @Test
    public void testFindRatingById() {
        Optional<Rating> ratingOptional = ratingDbStorage.findRatingById(1);

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(eachUser ->
                        assertThat(eachUser).hasFieldOrPropertyWithValue("name", "G")
                );
    }

}

