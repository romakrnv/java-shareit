package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.entity.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserMapperTest {
    private final UserDto updUser = new UserDto(1L, "john.doe@mail.com", "John Doe");
    private final User user = new User(1L, "John Doe", "john.doe@mail.com");
    private final UserDto dto = new UserDto(1L, "John Doe", "john.doe@mail.com");

    private final UserDto emptyUpdUser = new UserDto(1L, "", "");

    @Test
    public void toUserDtoTest() {
        UserDto userDto = UserMapper.toDto(user);
        assertThat(userDto, equalTo(dto));
    }

    @Test
    public void toUserTest() {
        UserDto newUser = new UserDto();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@mail.com");
        User us = UserMapper.toEntity(newUser);
        assertThat(us.getName(), equalTo(user.getName()));
        assertThat(us.getEmail(), equalTo(user.getEmail()));
        assertThat(us.getName(), equalTo(user.getName()));
    }

    @Test
    public void updateUserFieldsTest() {
        User us = UserMapper.update(user, updUser);
        assertThat(us.getId(), equalTo(user.getId()));
        assertThat(us.getName(), equalTo(user.getName()));
        assertThat(us.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void updateUserEmptyFieldsTest() {
        User us = UserMapper.update(user, emptyUpdUser);
        assertThat(us.getId(), equalTo(user.getId()));
        assertThat(us.getName(), equalTo(user.getName()));
        assertThat(us.getEmail(), equalTo(user.getEmail()));
    }
}