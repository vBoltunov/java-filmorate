package ru.yandex.practicum.filmorate.storage.db.user;

import java.util.List;

public interface FriendStorage {

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<Long> getFriendsIds(Long userId, boolean onlyConfirmed);

    List<Long> getCommonFriendsIds(Long userId1, Long userId2);
}
