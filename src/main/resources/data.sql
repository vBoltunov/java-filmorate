MERGE INTO mpa (id, name) VALUES (1, 'G');
MERGE INTO mpa (id, name) VALUES (2, 'PG');
MERGE INTO mpa (id, name) VALUES (3, 'PG-13');
MERGE INTO mpa (id, name) VALUES (4, 'R');
MERGE INTO mpa (id, name) VALUES (5, 'NC-17');
MERGE INTO genres (id, name) KEY(id)
    VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');