package ru.yandex.practicum.filmorate.dto.user.requests;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateUserRequest {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<Long> friendsIds;

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return birthday != null;
    }

    public boolean hasFriendsIds() {
        return ! (friendsIds == null || friendsIds.isEmpty());
    }
}
