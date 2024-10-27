package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public UserDto getUser(Long id) {
        User user = userDao.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return UserMapper.toDto(user);
    }

    public UserDto createUser(User user) {
        if (userDao.emailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        return UserMapper.toDto(userDao.create(user));
    }

    public UserDto updateUser(Long id, Map<String, Object> updates) {
        User user = userDao.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName((String) value);
                    break;
                case "email":
                    if (userDao.emailExist((String) value)) {
                        throw new EmailAlreadyExistsException((String) value);
                    }
                    user.setEmail((String) value);
                    break;
            }
        });
        return UserMapper.toDto(userDao.update(user));
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }
}