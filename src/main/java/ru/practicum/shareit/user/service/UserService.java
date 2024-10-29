package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

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

    public UserDto createUser(UserDto userDto) {
        if (userDao.emailExist(userDto.getEmail())) {
            throw new EmailAlreadyExistsException(userDto.getEmail());
        }
        return UserMapper.toDto(userDao.create(UserMapper.toEntity(userDto)));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User existedUser = userDao.get(id);
        if (existedUser == null) {
            throw new UserNotFoundException(id);
        }
        if (userDto.getName() != null) {
            existedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userDao.emailExist(userDto.getEmail())) {
                throw new EmailAlreadyExistsException(userDto.getEmail());
            }
            existedUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toDto(userDao.update(existedUser));
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }
}