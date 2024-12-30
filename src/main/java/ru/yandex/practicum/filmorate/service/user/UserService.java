package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    private void validateUsersExist(Long userId, Long friendId) {
        getUserByIdOrThrow(userId);
        getUserByIdOrThrow(friendId);
    }

    private User getUserByIdOrThrow(Long userId) {
        return userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %s not found", userId)));
    }

    private void initializeFriends(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Adding friend: userId={}, friendId={}", userId, friendId);
        validateUsersExist(userId, friendId);

        User user = getUserByIdOrThrow(userId);
        User userFriend = getUserByIdOrThrow(friendId);

        initializeFriends(user);
        initializeFriends(userFriend);

        user.getFriends().add(friendId);
        userFriend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);

        log.debug("Friend added successfully: user={}, friend={}", user, userFriend);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);

        User user = getUserByIdOrThrow(userId);
        User userFriend = getUserByIdOrThrow(friendId);

        initializeFriends(user);
        initializeFriends(userFriend);

        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);
    }

    public List<Map<String, Long>> getFriends(Long userId) {
        User user = getUserByIdOrThrow(userId);
        initializeFriends(user);
        return user.getFriends().stream()
                .map(friendId -> Collections.singletonMap("id", friendId))
                .toList();
    }

    public List<Map<String, Long>> getCommonFriends(Long userId, Long otherUserId) {
        validateUsersExist(userId, otherUserId);

        User user = getUserByIdOrThrow(userId);
        User otherUser = getUserByIdOrThrow(otherUserId);

        initializeFriends(user);
        initializeFriends(otherUser);

        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());

        return commonFriends.stream()
                .map(friendId -> Collections.singletonMap("id", friendId))
                .toList();
    }
}