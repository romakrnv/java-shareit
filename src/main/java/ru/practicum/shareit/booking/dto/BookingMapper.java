package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Statuses;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setItem(ItemMapper.toDto(booking.getItem()));
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(UserMapper.toDto(booking.getBooker()));

        return dto;
    }

    public static Booking toEntity(NewBookingDto request, User booker, Item item) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(Statuses.WAITING);
        booking.setBooker(booker);

        return booking;
    }
}
