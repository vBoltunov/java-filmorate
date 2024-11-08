package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

/**
 * User.
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
    Date birthday;
}