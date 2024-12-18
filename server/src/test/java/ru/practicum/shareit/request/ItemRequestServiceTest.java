package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @InjectMocks
    private UserService userService;

    @Test
    void testUpdateUserWhenUserWithSameEmail() {
        UpdateRequest updItemRequest = new UpdateRequest(1L, "description1", 1L,
                LocalDateTime.of(2023, 7, 3, 19, 30, 1));

        UserDto newUser = new UserDto();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        UserDto userDto = userService.createUser(newUser);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "john.doe@mail.com", "John Doe")));

        updItemRequest.setId(null);

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            itemRequestService.update(1L, updItemRequest);
        });

        assertEquals("request id should be specified", thrown.getMessage());
    }
}
