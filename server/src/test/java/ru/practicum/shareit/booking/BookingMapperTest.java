package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Statuses;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nextDay = LocalDateTime.now().plusDays(1);

    private final User user = new User(1L, "john.doe@mail.com", "John Doe");
    private final Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);

    private final UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

    private final NewBookingDto newBooking = new NewBookingDto(now, nextDay, 1L, 1L);
    private final BookingDto dto = new BookingDto(1L, now, nextDay, itemDto, Statuses.WAITING, userDto);
    private final Booking booking = new Booking(1L, now, nextDay, item, Statuses.WAITING, user);

    @Test
    public void toBookingDtoTest() {
        BookingDto bookingDto = BookingMapper.toDto(booking);
        assertThat(bookingDto, equalTo(dto));
    }

    @Test
    public void toBookingTest() {
        Booking b = BookingMapper.toEntity(newBooking, user, item);
        assertThat(b.getStart(), equalTo(booking.getStart()));
        assertThat(b.getEnd(), equalTo(booking.getEnd()));
        assertThat(b.getStatus(), equalTo(booking.getStatus()));
        assertThat(b.getItem(), equalTo(item));
        assertThat(b.getBooker(), equalTo(user));
    }
}
