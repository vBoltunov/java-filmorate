package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Name not provided. Using login instead of name.");
        }

        user.setId(getNextId());
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        log.info("User created successfully: {}", user);

        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Validation failed: ID must be provided.");
            throw new IllegalArgumentException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            for (User savedUser : users.values()) {
                if (!Objects.equals(savedUser.getId(), newUser.getId()) && Objects.equals(savedUser.getEmail(), newUser.getEmail())) {
                    log.warn("Validation failed: Email is already in use.");
                    throw new IllegalArgumentException("Email is already in use");
                }
            }
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            if (newUser.getFriends() == null) {
                oldUser.setFriends(new HashSet<>());
            } else {
                oldUser.setFriends(newUser.getFriends());
            }

            users.put(newUser.getId(), oldUser);
            log.info("User updated successfully: {}", oldUser);

            return oldUser;
        }
        log.error("User not found: id = {}", newUser.getId());
        throw new NotFoundException("User with id = " + newUser.getId() + " not found");
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
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