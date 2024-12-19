package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Statuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
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
class BookingServiceTest {
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

    @InjectMocks
    private BookingService bookingService;

    @Test
    void testCreateBookingWhenItemNotAvailable() {
        NewBookingDto newBooking = new NewBookingDto(LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), 1L, 2L);

        UserDto newUser = new UserDto();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        UserDto userDto = userService.createUser(newUser);
        User user = new User(1L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // item
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(false).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.FALSE, user, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, newItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(1L, newBooking);
        });

        assertEquals("Item is not available", thrown.getMessage());
    }

    @Test
    void testCreateBookingWhenItemUserIsBooker() {
        NewBookingDto newBooking = new NewBookingDto(LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), 1L, 2L);

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
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, itemDto);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(1L, newBooking);
        });

        assertEquals("You can't book your own item", thrown.getMessage());
    }

    @Test
    void testFindBookingWhenUserNotItemOwnerOrBooker() {
        // user 1
        UserDto newUser1 = new UserDto();
        newUser1.setEmail("john.doe@mail.com");
        newUser1.setName("John Doe");

        when(userRepository.findByEmail(newUser1.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        userService.createUser(newUser1);
        User user1 = new User(1L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        // user 2
        UserDto newUser2 = new UserDto();
        newUser2.setName("John Doe");
        newUser2.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser2.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(2L, "john.doe@mail.com", "John Doe"));

        userService.createUser(newUser2);
        User user2 = new User(2L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        // item
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, newItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // booking
        NewBookingDto newBooking = new NewBookingDto(LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), 1L, 2L);
        Booking booking = new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), item, Statuses.WAITING, user2);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingItem = bookingService.create(2L, newBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        PermissionException thrown = assertThrows(PermissionException.class, () -> {
            bookingService.findBooking(1L, 999L);
        });

        assertEquals(1L + ": don't have permission to access this resource.", thrown.getMessage());
    }

    @Test
    void testApproveBookingWithWrongBookerOrOwner() {
        // user 1
        UserDto newUser1 = new UserDto();
        newUser1.setName("John Doe");
        newUser1.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser1.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        userService.createUser(newUser1);
        User user1 = new User(1L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        // user 2
        UserDto newUser2 = new UserDto();
        newUser2.setName("John Doe");
        newUser2.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser2.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(2L, "john.doe@mail.com", "John Doe"));

        userService.createUser(newUser2);
        User user2 = new User(2L, "john.doe@mail.com", "John Doe");


        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        // item
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, newItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // booking
        NewBookingDto newBooking = new NewBookingDto(LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), 1L, 2L);
        Booking booking = new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), item, Statuses.WAITING, user2);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingItem = bookingService.create(2L, newBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        PermissionException thrown = assertThrows(PermissionException.class, () -> {
            bookingService.approveBooking(1L, 2L, Boolean.TRUE);
        });

        assertEquals(2L + ": don't have permission to access this resource.", thrown.getMessage());
    }

    @Test
    void testApproveBookingWhenStatusNotWaiting() {
        // user 1
        UserDto newUser1 = new UserDto();
        newUser1.setName("John Doe");
        newUser1.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser1.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(1L, "john.doe@mail.com", "John Doe"));

        userService.createUser(newUser1);
        User user1 = new User(1L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        // user 2
        UserDto newUser2 = new UserDto();
        newUser2.setName("John Doe");
        newUser2.setEmail("john.doe@mail.com");

        when(userRepository.findByEmail(newUser2.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(2L, "john.doe@mail.com", "John Doe"));

        userService.createUser(newUser2);
        User user2 = new User(2L, "john.doe@mail.com", "John Doe");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        // item
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user1, 1L);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto findItem = itemService.create(1L, newItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // booking
        NewBookingDto newBooking = new NewBookingDto(LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), 1L, 2L);
        Booking booking = new Booking(1L, LocalDateTime.of(2024, 7, 1, 19, 30, 15),
                LocalDateTime.of(2024, 7, 2, 19, 30, 15), item, Statuses.APPROVED, user2);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingItem = bookingService.create(2L, newBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingStatusException thrown = assertThrows(BookingStatusException.class, () -> {
            bookingService.approveBooking(1L, 1L, Boolean.FALSE);
        });

        assertEquals("Item Already reserved for booking " + 1L, thrown.getMessage());
    }
}
