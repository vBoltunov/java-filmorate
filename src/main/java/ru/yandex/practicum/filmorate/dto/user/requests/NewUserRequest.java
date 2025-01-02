package ru.yandex.practicum.filmorate.dto.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.user.FriendDto;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewUserRequest {
    @Email(message = "Email is not valid")
    private String email;
    @NotBlank(message = "Login name must not be blank")
    @Pattern(regexp = "\\S+", message = "Login name must not contain spaces")
    private String login;
    private String name;
    @Past(message = "Birth date must not be in the future")
    private LocalDate birthday;
    private List<FriendDto> friends;
}
