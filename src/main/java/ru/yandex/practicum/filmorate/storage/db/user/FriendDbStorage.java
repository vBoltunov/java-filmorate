package ru.yandex.practicum.filmorate.storage.db.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.Friend;
import ru.yandex.practicum.filmorate.model.user.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.db.BaseDbStorage;
import ru.yandex.practicum.filmorate.util.FormatUtil;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class FriendDbStorage extends BaseDbStorage<Friend> implements FriendStorage {

    public FriendDbStorage(JdbcTemplate jdbc) {
        super(jdbc);
    }

    private static final String ADD_FRIEND_QUERY = """
        INSERT INTO friends (user_id, friend_id, status)
        VALUES (?, ?, ?);
        """;
    private static final String REMOVE_FRIEND_QUERY = """
        DELETE FROM friends
        WHERE user_id = ? AND friend_id = ?;
        """;
    private static final String GET_FRIENDS_QUERY = """
        SELECT friend_id
        FROM friends
        WHERE user_id = ?
          AND (status = 'CONFIRMED' OR ? = false);
        """;
    private static final String GET_COMMON_FRIENDS_QUERY = """
        SELECT f1.friend_id
        FROM friends f1
        JOIN friends f2 ON f1.friend_id = f2.friend_id
        WHERE f1.user_id = ? AND f2.user_id = ?;
        """;
    private static final String CHECK_USER_EXISTS_QUERY = """
        SELECT EXISTS(SELECT 1
                      FROM users
                      WHERE id = ?);
        """;
    private static final String CHECK_FRIENDSHIP_EXISTS_QUERY = """
        SELECT COUNT(*)
        FROM friends
        WHERE user_id = ? AND friend_id = ?;
        """;
    private static final String UPDATE_FRIENDSHIP_STATUS_QUERY = """
        UPDATE friends
        SET status = ?
        WHERE user_id = ? AND friend_id = ?;
        """;

    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            validateUserExists(userId);
            validateUserExists(friendId);

            if (isFriendshipExists(userId, friendId)) {
                log.debug("User with id={} already has friend with id={}", userId, friendId);
                return;
            }

            update(
                    ADD_FRIEND_QUERY,
                    userId,
                    friendId,
                    FriendshipStatus.UNCONFIRMED.name()
            );

            if (isFriendshipExists(friendId, userId)) {
                updateFriendshipStatus(userId, friendId, FriendshipStatus.CONFIRMED);
                updateFriendshipStatus(friendId, userId, FriendshipStatus.CONFIRMED);
            }
        } catch (DataAccessException e) {
            log.error("Error adding friend", e);
            throw new InternalServerException(
                    "Failed to add friend: " + e.getMessage());
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        try {
            validateUserExists(userId);
            validateUserExists(friendId);

            jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);

            if (isFriendshipExists(friendId, userId)) {
                updateFriendshipStatus(friendId, userId, FriendshipStatus.UNCONFIRMED);
            }
        } catch (DataAccessException e) {
            log.error("Error removing friend", e);
            throw new InternalServerException(
                    "Failed to remove friend: " + e.getMessage());
        }
    }

    @Override
    public List<Long> getFriendsIds(Long userId, boolean onlyConfirmed) {
        validateUserExists(userId);
        return jdbc.queryForList(
                GET_FRIENDS_QUERY,
                Long.class,
                userId,
                !onlyConfirmed
        );
    }

    @Override
    public List<Long> getCommonFriendsIds(Long userId1, Long userId2) {
        validateUserExists(userId1);
        validateUserExists(userId2);

        return jdbc.queryForList(
                GET_COMMON_FRIENDS_QUERY,
                Long.class,
                userId1,
                userId2
        );
    }

    public void validateUserExists(Long userId) {
        boolean userExists = Optional.ofNullable(
                jdbc.queryForObject(
                        CHECK_USER_EXISTS_QUERY,
                        Boolean.class,
                        userId
                )
        ).orElse(false);

        if (!userExists) {
            throw new NotFoundException(
                    FormatUtil.formatNotFoundMessage("User", userId));
        }
    }

    public boolean isFriendshipExists(Long userId1, Long userId2) {
        Integer count = jdbc.queryForObject(
                CHECK_FRIENDSHIP_EXISTS_QUERY,
                Integer.class,
                userId1,
                userId2
        );
        return count != null && count > 0;
    }

    public void updateFriendshipStatus(Long userId1, Long userId2, FriendshipStatus status) {
        update(
                UPDATE_FRIENDSHIP_STATUS_QUERY,
                status.name(),
                userId1,
                userId2
        );
    }
}
