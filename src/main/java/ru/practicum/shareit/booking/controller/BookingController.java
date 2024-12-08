package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{booking-id}")
    public BookingDto findBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("booking-id") Long bookingId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> findAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL")
                                                        String state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllBookingsByOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL")
                                                              String state) {
        return bookingService.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody NewBookingDto booking) {
        return bookingService.create(userId, booking);
    }

    @DeleteMapping("/{booking-id}")
    public void delete(@PathVariable("booking-id") Long bookingId) {
        bookingService.delete(bookingId);
    }

    @PatchMapping("/{booking-id}")
    public BookingDto approveBooking(@PathVariable("booking-id") Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }
}
