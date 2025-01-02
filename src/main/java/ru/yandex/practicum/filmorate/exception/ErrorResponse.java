package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

public class ErrorResponse {
    @Getter
    int error;
    String description;


    public ErrorResponse(int error, String description) {
        this.error = error;
        this.description = description;
    }
}