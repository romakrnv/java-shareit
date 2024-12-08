package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingDto {
    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    @NotNull
    @Positive
    private Long itemId;

    @Positive
    private Long bookerId;
}
