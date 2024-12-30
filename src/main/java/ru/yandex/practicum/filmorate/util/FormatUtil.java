package ru.yandex.practicum.filmorate.util;

public class FormatUtil {
    private static final String NOT_FOUND_TEMPLATE = "%s with id %s not found";

    private FormatUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String formatNotFoundMessage(String entity, Long id) {
        return String.format(NOT_FOUND_TEMPLATE, entity, id);
    }
}
