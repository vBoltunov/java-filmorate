CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    email    VARCHAR(255) UNIQUE NOT NULL,
    login    VARCHAR(255) UNIQUE NOT NULL,
    name     VARCHAR(255),
    birthday DATE                NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    release_date  DATE         NOT NULL,
    duration      BIGINT       NOT NULL,
    mpa_rating_id BIGINT
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   BIGINT,
    friend_id BIGINT,
    status    VARCHAR(20) DEFAULT 'UNCONFIRMED',
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, genre_id)
);


CREATE TABLE IF NOT EXISTS mpa
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

ALTER TABLE films
    ADD CONSTRAINT IF NOT EXISTS fk_films_mpa_rating_id FOREIGN KEY (mpa_rating_id) REFERENCES mpa (id);

ALTER TABLE friends
    ADD CONSTRAINT IF NOT EXISTS fk_friends_user_id FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE friends
    ADD CONSTRAINT IF NOT EXISTS fk_friends_friend_id FOREIGN KEY (friend_id) REFERENCES users (id);

ALTER TABLE likes
    ADD CONSTRAINT IF NOT EXISTS fk_likes_film_id FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE likes
    ADD CONSTRAINT IF NOT EXISTS fk_likes_user_id FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE film_genres
    ADD CONSTRAINT IF NOT EXISTS fk_film_genres_film_id FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE film_genres
    ADD CONSTRAINT IF NOT EXISTS fk_film_genres_genre_id FOREIGN KEY (genre_id) REFERENCES genres (id);
