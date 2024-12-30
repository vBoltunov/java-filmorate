package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.user.UserController;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.ValidationUtil;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    @Test
    void createUser_WithInvalidEmail() {
        user.setName("Valid Name");
        user.setEmail("example.example.com");
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setLogin("ValidLogin");

        Set<ConstraintViolation<User>> violations = ValidationUtil.validate(user);

        assertFalse(violations.isEmpty(), "Expected validation violations but none found.");
        assertEquals("Email is not valid",
                ValidationUtil.getFirstViolationMessage(violations));
    }

    @Test
    void createUser_WithEmptyLogin() {
        user.setName("Valid Name");
        user.setEmail("example@example.com");
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = ValidationUtil.validate(user);

        assertFalse(violations.isEmpty(), "Expected validation violations but none found.");
        assertTrue(violations.stream()
                        .anyMatch(v -> "Login name must not be blank".equals(v.getMessage())),
                "Expected violation message not found: Login name must not be blank");
    }

    @Test
    void createUser_LoginWithSpaces() {
        user.setName("Valid Name");
        user.setEmail("example@example.com");
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setLogin("Invalid Login");

        Set<ConstraintViolation<User>> violations = ValidationUtil.validate(user);

        assertFalse(violations.isEmpty(), "Expected validation violations but none found.");
        assertTrue(violations.stream()
                        .anyMatch(v -> "Login name must not contain spaces".equals(v.getMessage())),
                "Expected violation message not found: Login name must not be blank");
    }

    @Test
    void createUser_WithEmptyName() {
        user.setName("");
        user.setEmail("example@example.com");
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setLogin("ValidLogin");

        userController.createUser(user);

        assertEquals("ValidLogin", user.getName());
    }

    @Test
    void createUser_WithFutureBirthDate() {
        user.setName("Valid Name");
        user.setEmail("example@example.com");
        user.setBirthday(LocalDate.of(2035, 1, 1));
        user.setLogin("ValidLogin");

        Set<ConstraintViolation<User>> violations = ValidationUtil.validate(user);

        assertFalse(violations.isEmpty(), "Expected validation violations but none found.");
        assertEquals("Birth date must not be in the future", ValidationUtil.getFirstViolationMessage(violations));
    }
}