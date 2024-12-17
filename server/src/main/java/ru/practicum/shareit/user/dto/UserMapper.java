package ru.practicum.shareit.user.dto;

import io.micrometer.common.util.StringUtils;
import ru.practicum.shareit.user.entity.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static User update(User user, UserDto dto) {
        if (dto == null) {
            return null;
        }
        if (!StringUtils.isBlank(dto.getName())) {
            user.setName(dto.getName());
        }
        if (!StringUtils.isBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        return user;
    }
}