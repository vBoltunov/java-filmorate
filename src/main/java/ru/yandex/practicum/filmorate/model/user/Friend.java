package ru.yandex.practicum.filmorate.model.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.user.enums.FriendshipStatus;

/**
 * Represents a friendship relationship between two users.
 *
 * This class includes attributes such as the user's ID, the friend's ID, and the status of the friendship.
 * It uses the `@Data` annotation to automatically generate boilerplate code like getters, setters, and constructors.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Friend {
    Long userId;
    Long friendId;
    FriendshipStatus status;
}
