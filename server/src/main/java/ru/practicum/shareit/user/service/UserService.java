package ru.practicum.shareit.user.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserDto getUser(Long id) {
        return UserMapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id)));
    }

    public UserDto createUser(UserDto userDto) {
        if (repository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(userDto.getEmail());
        }
        return UserMapper.toDto(repository.save(UserMapper.toEntity(userDto)));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        if (!StringUtils.isBlank(userDto.getEmail()) && repository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(userDto.getEmail());
        }
        return UserMapper.toDto(repository.save(UserMapper.update(findById(id), userDto)));
    }

    public void deleteUser(Long id) {
        repository.delete(findById(id));
    }

    private User findById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(User.class, userId));
    }
}