package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.enums.States;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    private final String headerUserId = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> findBooking(@RequestHeader(headerUserId) Long userId,
                                              @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByUser(@RequestHeader(headerUserId) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        States state = States.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookings(null, userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsByOwnerItems(@RequestHeader(headerUserId) Long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        States state = States.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookings("/owner", userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(headerUserId) Long userId,
                                           @Valid @RequestBody NewBookingRequest requestDto) {
        return bookingClient.bookItem(userId, requestDto);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> deleteBooking(@PathVariable Long bookingId) {
        return bookingClient.deleteBooking(bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                                 @RequestHeader(headerUserId) Long userId,
                                                 @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }
}