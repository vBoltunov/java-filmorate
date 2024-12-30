package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.db.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.film.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.db.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate")
@Import({FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class, UserDbStorage.class})
class FilmorateApplicationTests {

	private final FilmDbStorage filmStorage;
	private final GenreDbStorage genreStorage;
	private final MpaDbStorage mpaStorage;
	private final UserDbStorage userStorage;

	@BeforeEach
	public void setUp() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testUser");
		user.setName("Test User");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		userStorage.createUser(user);

		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(120L);
		film.setMpa(new Mpa(1L, "G"));
		film.setGenreIds(List.of(1L, 2L));
		filmStorage.createFilm(film);
	}

	// User Storage Tests
	@Test
	void testFindUserById() {
		Optional<User> userOptional = userStorage.getUserById(1L);
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
	}

	@Test
	void testGetUsers() {
		Collection<User> users = userStorage.getUsers();
		assertThat(users).isNotEmpty();
	}

	@Test
	void testCreateUser() {
		User newUser = new User();
		newUser.setEmail("newuser@example.com");
		newUser.setLogin("newUser");
		newUser.setName("New User");
		newUser.setBirthday(LocalDate.of(1995, 5, 5));
		User createdUser = userStorage.createUser(newUser);
		assertThat(createdUser).isNotNull();
		assertThat(createdUser.getId()).isPositive();
	}

	// Film Storage Tests
	@Test
	void testFindFilmById() {
		Optional<Film> filmOptional = filmStorage.getFilmById(1L);
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 1L));
	}

	@Test
	void testGetFilms() {
		Collection<Film> films = filmStorage.getFilms();
		assertThat(films).isNotEmpty();
	}

	@Test
	void testCreateFilm() {
		Film newFilm = new Film();
		newFilm.setName("Another Test Film");
		newFilm.setDescription("Another Description");
		newFilm.setReleaseDate(LocalDate.of(2021, 1, 1));
		newFilm.setDuration(150L);
		newFilm.setMpa(new Mpa(1L, "G"));
		newFilm.setGenreIds(List.of(1L, 2L));
		Film createdFilm = filmStorage.createFilm(newFilm);
		assertThat(createdFilm).isNotNull();
		assertThat(createdFilm.getId()).isPositive();
	}

	// Genre Storage Tests
	@Test
	void testGetAllGenres() {
		Collection<Genre> genres = genreStorage.getAllGenres();
		assertThat(genres).isNotEmpty();
	}

	@Test
	void testGetGenreById() {
		Optional<Genre> genreOptional = genreStorage.getGenreById(1L);
		assertThat(genreOptional)
				.isPresent()
				.hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("id", 1L));
	}

	// MPA Storage Tests
	@Test
	void testGetMpaById() {
		Optional<Mpa> mpaOptional = mpaStorage.getMpaById(1L);
		assertThat(mpaOptional)
				.isPresent()
				.hasValueSatisfying(mpa -> assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L));
	}

	@Test
	void testGetAllMpas() {
		Collection<Mpa> mpas = mpaStorage.getMpas();
		assertThat(mpas).isNotEmpty();
	}
}