package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.user.enums.FriendshipStatus;

/**
 * Represents a friendship relationship between two users.
 *
 * This class includes attributes such as the user's ID, the friend's ID, and the status of the friendship.
 * It uses the `@Data` annotation to automatically generate boilerplate code like getters, setters, and constructors.
 */
@Data
public class Friend {
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;
}
