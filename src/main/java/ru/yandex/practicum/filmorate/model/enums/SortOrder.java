package ru.yandex.practicum.filmorate.model.enums;

public enum SortOrder {
    ASCENDING, DESCENDING;

    public static SortOrder sortOrder(String order) {
        return switch (order.toLowerCase()) {
            case "ascending", "asc" -> ASCENDING;
            case "descending", "desc" -> DESCENDING;
            default -> null;
        };
    }
}
