package ru.yandex.practicum.filmorate.storage.db.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> findByEmail(String email);
}