package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Collection;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String email;
    String login;
    String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocalDate birthday;
    Collection<Long> friendsIds;
}
