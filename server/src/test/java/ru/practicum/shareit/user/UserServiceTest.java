package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testUserByIdWhenUserIdIsNull() {
        when((userRepository).findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(2L));
    }

    @Test
    void testCreateUserWhenEMailExist() {
        UserDto newUser = new UserDto();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(new User(1L, "Some User", "john.doe@mail.com")));

        EmailAlreadyExistsException thrown = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(newUser);
        });

        assertEquals("email: '" + "john.doe@mail.com" + "' already exist", thrown.getMessage());
    }

    @Test
    void testUpdateUserWhenUserWithSameEmail() {
        UserDto newUser = new UserDto(1L, "John Doe", "john.doe@mail.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(new User(1L, "Some User", "john.doe@mail.com")));

        EmailAlreadyExistsException thrown = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(1L, newUser);
        });

        assertEquals("email: '" + "john.doe@mail.com" + "' already exist", thrown.getMessage());
    }
}
