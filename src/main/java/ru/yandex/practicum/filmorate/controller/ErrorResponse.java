package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;

public class ErrorResponse {
    @Getter
    String error;
    String description;


    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}