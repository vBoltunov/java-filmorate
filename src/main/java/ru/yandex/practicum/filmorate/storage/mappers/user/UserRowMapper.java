package ru.yandex.practicum.filmorate.storage.mappers.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());

        String friendsIds = resultSet.getString("friends_ids");

        if (friendsIds != null && !friendsIds.isEmpty()) {
            List<Long> friendsList = Arrays.stream(friendsIds.split(","))
                    .map(Long::valueOf)
                    .toList();
            user.setFriendsIds(friendsList);
        } else {
            user.setFriendsIds(Collections.emptyList());
        }

        return user;
    }
}
