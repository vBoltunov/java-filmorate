package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.db.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;
import ru.yandex.practicum.filmorate.util.FormatUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public void addFriend(Long userId, Long friendId) {
        friendStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        friendStorage.removeFriend(userId, friendId);
    }

    public List<UserDto> getFriends(Long userId, boolean onlyConfirmed) {
        List<Long> friendIds = friendStorage.getFriendsIds(userId, onlyConfirmed);
        return friendIds.stream()
                .map(friendId -> userStorage.getUserById(friendId)
                        .map(UserMapper::mapToUserDto)
                        .orElseThrow(() -> new NotFoundException(
                                FormatUtil.formatNotFoundMessage("Friends for user", userId))))
                .toList();
    }

    public List<UserDto> getCommonFriends(Long userId, Long otherId) {
        List<Long> commonFriendIds = friendStorage.getCommonFriendsIds(userId, otherId);
        return commonFriendIds.stream()
                .map(friendId -> userStorage.getUserById(friendId)
                        .map(UserMapper::mapToUserDto)
                        .orElseThrow(() -> new NotFoundException(
                                String.format("Common friends for userId %s and friendId %s not found",
                                        userId, friendId))))
                .toList();
    }
}
