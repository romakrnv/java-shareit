package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemService itemService;

    @InjectMocks
    private UserService userService;

    @Test
    void testAddCommentWhenCommentIsEmpty() {
        NewCommentDto newComment = new NewCommentDto();
        newComment.setText("comment");
        newComment.setItemId(1L);
        newComment.setAuthorId(1L);

        // user
        UserDto newUser = new UserDto();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@mail.com");


        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        UserDto userDto = userService.createUser(newUser);
        User user = new User(1L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // item
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, newItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        //check
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(Boolean.FALSE);
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            itemService.addComment(1L, 1L, newComment);
        });

        assertEquals(String.format("User %s cannot create comment , because did not use the item %s", user.getId(), findItem.getName()), thrown.getMessage());
    }

    @Test
    void testDeleteItemWithWrongOwnerId() {
        // user
        UserDto newUser = new UserDto();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        UserDto userDto = userService.createUser(newUser);
        User user = new User(1L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // item
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, newItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        PermissionException thrown = assertThrows(PermissionException.class, () -> {
            itemService.delete(2L, 1L);
        });

        assertEquals(2L + ": don't have permission to access this resource.", thrown.getMessage());
    }

    @Test
    void testUpdateItemWithWrongOwnerId() {
        // user
        UserDto newUser = new UserDto();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        UserDto userDto = userService.createUser(newUser);
        User user = new User(1L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // item
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, newItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        PermissionException thrown = assertThrows(PermissionException.class, () -> {
            itemService.update(1L, null, 2L);
        });

        assertEquals(2L + ": don't have permission to access this resource.", thrown.getMessage());
    }

    @Test
    void testFindItemsForTenantWithBlankText() {
        Collection<ItemDto> items = itemService.findItemsForTenant(1L, null);

        assertEquals(items, new ArrayList<>());
    }

    @Test
    void testFindAllWithWrongOwnerId() {
        when(itemRepository.findAllByUserId(anyLong())).thenReturn(new ArrayList<>());

        Collection<FullItemDto> items = itemService.findAll(999L);

        assertEquals(new ArrayList<>(), items);
    }
}
