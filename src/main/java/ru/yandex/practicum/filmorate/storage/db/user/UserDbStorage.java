package ru.yandex.practicum.filmorate.storage.db.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.db.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = """
            SELECT u.*,
                   GROUP_CONCAT(f.friend_id SEPARATOR ',') AS friends_ids
            FROM users u
            LEFT JOIN friends f ON u.id = f.user_id
            GROUP BY u.id;
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO users(name, email, login, birthday)
            VALUES (?, ?, ?, ?);
            """;
    private static final String UPDATE_QUERY = """
            UPDATE users
            SET name = ?, email = ?, login = ?, birthday = ?
            WHERE id = ?;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT u.*, GROUP_CONCAT(f.friend_id SEPARATOR ',') AS friends_ids
            FROM users u
            LEFT JOIN friends f ON u.id = f.user_id
            WHERE u.id = ?
            GROUP BY u.id;
            """;
    private static final String FIND_BY_EMAIL_QUERY = """
            SELECT *
            FROM users
            WHERE email = ?;
            """;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User createUser(User user) {
        try {
            long id = insert(
                    INSERT_QUERY,
                    user.getName(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getBirthday());
            user.setId(id);
            return user;
        } catch (DuplicateKeyException e) {
            throw new DuplicatedDataException("User with this email or login already exists.");
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new InternalServerException("An error occurred while creating the user.");
        }
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday().toString(),
                user.getId()
        );
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }
}
