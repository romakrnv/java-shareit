package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Statuses;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingJsonTest {
    @Autowired
    private JacksonTester<BookingDto> bookingJson;

    @Test
    void testBookingDto() throws Exception {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime nextDay = LocalDateTime.now().plusDays(1);

        final ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 2L, 2L);
        final UserDto userDto = new UserDto(1L, "ivan@email", "Ivan Ivanov");
        final BookingDto bookingDto = new BookingDto(1L, now, nextDay, itemDto, Statuses.CANCELED, userDto);

        JsonContent<BookingDto> result = bookingJson.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(userDto.getEmail());
    }
}
