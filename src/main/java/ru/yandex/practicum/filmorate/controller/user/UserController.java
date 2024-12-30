package ru.yandex.practicum.filmorate.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.user.requests.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.requests.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.user.FriendService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FriendService friendService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        log.info("Fetching all users.");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("Fetching user with id: {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody NewUserRequest userRequest) {
        log.info("Creating new user: {}", userRequest);
        return userService.createUser(userRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody UpdateUserRequest request) {
        Long userId = request.getId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        log.info("Updating user with id: {}", userId);
        return userService.updateUser(userId, request);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        log.info("Adding friend: friendId={}, userId={}", friendId, userId);
        friendService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        log.info("Deleting friend: friendId={}, userId={}", friendId, userId);
        friendService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFriends(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "true") boolean onlyConfirmed
    ) {
        log.info("Fetching friends for user with id: {}", userId);
        return friendService.getFriends(userId, onlyConfirmed);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getCommonFriends(
            @PathVariable Long userId,
            @PathVariable Long otherUserId
    ) {
        log.info("Fetching common friends for users with id: {} and {}", userId, otherUserId);
        return friendService.getCommonFriends(userId, otherUserId);
    }
}

