package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Fetching all users.");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getEmail());
            log.info("Name not provided. Using email instead of name.");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("User created successfully: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Updating user with id: {}", newUser.getId());

        if (newUser.getId() == null) {
            log.warn("Validation failed: ID must be provided.");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            for (User savedUser : users.values()) {
                if (Objects.equals(savedUser.getEmail(), newUser.getEmail())) {
                    log.warn("Validation failed: Email is already in use.");
                    throw new ValidationException("Email is already in use");
                }
            }
            oldUser.setEmail(newUser.getEmail());
            log.info("User updated successfully: {}", oldUser);
            return oldUser;
        }
        log.error("User not found: id = {}", newUser.getId());
        throw new NotFoundException("User with id = " + newUser.getId() + " not found");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
