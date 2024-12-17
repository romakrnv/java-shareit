package ru.practicum.shareit.exception;

public class BookingStatusException extends RuntimeException {
    public BookingStatusException(Long id) {
        super("Item Already reserved for booking " + id);
    }
}
