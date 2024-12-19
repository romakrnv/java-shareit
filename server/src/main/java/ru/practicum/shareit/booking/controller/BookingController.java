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

    private final String headerUserId = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(@RequestHeader(headerUserId) Long userId,
                                  @PathVariable Long bookingId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> findAllBookingsByUser(@RequestHeader(headerUserId) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL")
                                                        String state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllBookingsByOwnerItems(@RequestHeader(headerUserId) Long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL")
                                                              String state) {
        return bookingService.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto bookItem(@RequestHeader(headerUserId) Long userId,
                             @Valid @RequestBody NewBookingDto booking) {
        return bookingService.create(userId, booking);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        bookingService.delete(bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestHeader(headerUserId) Long userId,
                                     @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }
}