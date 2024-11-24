package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user with its essential details.
 *
 * This class includes attributes such as the user's email, login, name, birthday and list of user's friends' IDs.
 * It uses validation annotations to ensure that the user's email is a valid email address,
 * the login is not null or blank and contains no whitespaces,
 * and the birthday is a date in the past.
 */
@Data
public class User {
    Long id;

    @Email(message = "Email is not valid")
    String email;

    @NotBlank(message = "Login name must not be blank")
    @Pattern(regexp = "\\S+", message = "Login name must not contain spaces")
    String login;

    String name;

    @Past(message = "Birth date must not be in the future")
    LocalDate birthday;

    Set<Long> friends = new HashSet<>();
}