package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewBookingRequest {

    LocalDateTime start;

    LocalDateTime end;

    @NotNull
    @Positive
    Long itemId;

    @Positive
    Long bookerId;
}
