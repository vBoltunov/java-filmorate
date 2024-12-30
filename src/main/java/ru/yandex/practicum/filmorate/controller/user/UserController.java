package ru.yandex.practicum.filmorate.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Fetching all users.");
        return userStorage.getUsers();
    }

    @GetMapping("/{userId}")
    public Optional<User> getUser(@PathVariable Long userId) {
        return userStorage.getUserById(userId);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<Map<String, Long>>> getFriends(@PathVariable Long id) {
        log.info("Fetching friends for user with id: {}", id);
        List<Map<String, Long>> friends = userService.getFriends(id);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public ResponseEntity<List<Map<String, Long>>> getCommonFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Fetching common friends for users with id: {} and {}", id, friendId);
        List<Map<String, Long>> commonFriends = userService.getCommonFriends(id, friendId);
        return new ResponseEntity<>(commonFriends, HttpStatus.OK);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Updating user with id: {}", user.getId());
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Adding friend with id: {} to user with id: {}", friendId, id);
        userService.addFriend(id, friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Deleting friend with id: {}", friendId);
        userService.removeFriend(id, friendId);
        userService.removeFriend(friendId, id);
    }
}