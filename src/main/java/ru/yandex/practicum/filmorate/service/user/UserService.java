package ru.yandex.practicum.filmorate.service.user;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.requests.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.requests.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.user.UserMapper;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;
import ru.yandex.practicum.filmorate.util.FormatUtil;
import ru.yandex.practicum.filmorate.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto createUser(NewUserRequest request) {
        try {
            Optional<User> alreadyExistUser = userStorage.findByEmail(request.getEmail());
            if (alreadyExistUser.isPresent()) {
                throw new DuplicatedDataException("Email already in use");
            }

            User user = UserMapper.mapToUser(request);

            Set<ConstraintViolation<User>> violations = ValidationUtil.validate(user);
            if (!violations.isEmpty()) {
                throw new ConditionsNotMetException(ValidationUtil.getFirstViolationMessage(violations));
            }

            user = userStorage.createUser(user);
            return UserMapper.mapToUserDto(user);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw e;
        }
    }

    public UserDto getUserById(long userId) {
        return userStorage.getUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException(
                        FormatUtil.formatNotFoundMessage("User", userId)));
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {
        try {
            User updatedUser = userStorage.getUserById(userId)
                    .map(user -> UserMapper.updateUserFields(user, request))
                    .orElseThrow(() -> new NotFoundException(
                            FormatUtil.formatNotFoundMessage("User", userId)));
            updatedUser = userStorage.updateUser(updatedUser);
            return UserMapper.mapToUserDto(updatedUser);
        } catch (NotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            throw new NotFoundException(
                    FormatUtil.formatNotFoundMessage("User", userId));
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            throw e;
        }
    }
}
