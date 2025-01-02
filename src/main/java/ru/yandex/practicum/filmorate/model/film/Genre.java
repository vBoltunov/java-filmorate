package ru.yandex.practicum.filmorate.model.film;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a genre with its essential details.
 *
 * This class includes attributes such as the genre's ID and name.
 * It uses the `@Data`, `@Entity`, `@AllArgsConstructor`, and `@NoArgsConstructor` annotations to automatically
 * generate boilerplate code like getters, setters, constructors, and to map the class to a database table.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    @Id
    Long id;
    String name;
}
