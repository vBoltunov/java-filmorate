package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ConditionsNotMetException extends RuntimeException {
    public ConditionsNotMetException(String message) {
        super(message);
    }
}