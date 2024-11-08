package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Date;

/**
 * User.
 */
@Data
public class User {
    Long id;
    String email;
    String login;
    String name;
    Date birthday;
}