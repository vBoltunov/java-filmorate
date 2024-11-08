package ru.yandex.practicum.filmorate.controller;

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

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
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
    public User create(@RequestBody User user) {
        log.info("Creating user: {}", user);

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Validation failed: Email must be provided.");
            throw new ValidationException("Эл. почта должна быть указана");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Validation failed: Email is not valid.");
            throw new ValidationException("Некорректная эл. почта");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Validation failed: Login name must not be blank.");
            throw new ValidationException("Логин должен быть указан");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Validation failed: Login name must not contain whitespaces.");
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Name not provided. Using email instead of name.");
            user.setName(user.getEmail());
        }

        if (user.getBirthday().after(Date.from(Instant.now()))) {
            log.warn("Validation failed: Birth date must not be in the future.");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("User created successfully: {}", user);

        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Updating user with id: {}", newUser.getId());

        if (newUser.getId() == null) {
            log.warn("Validation failed: ID must be provided.");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            for(User savedUser : users.values()) {
                if (Objects.equals(savedUser.getEmail(), newUser.getEmail())) {
                    log.warn("Validation failed: Email is already in use.");
                    throw new ValidationException("Эта эл. почта уже используется");
                }
            }
            oldUser.setEmail(newUser.getEmail());
            log.info("User updated successfully: {}", oldUser);

            return oldUser;
        }
        log.error("User not found: id = {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
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
