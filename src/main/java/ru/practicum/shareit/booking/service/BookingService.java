package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.States;
import ru.practicum.shareit.booking.enums.Statuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BookingService {
    final BookingRepository repository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Transactional
    public BookingDto create(Long userId, NewBookingDto request) {
        Item findItem = findItemById(request.getItemId());
        User findUser = findUserById(userId);

        if (!findItem.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        if (findUser.getId().equals(findItem.getUser().getId())) {
            throw new ValidationException("You can't book your own item");
        }

        Booking booking = BookingMapper.toEntity(request, findUser, findItem);
        booking = repository.save(booking);

        return BookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    public BookingDto findBooking(Long bookingId, Long userId) {
        Booking booking = findById(bookingId);
        User owner = findUserById(booking.getItem().getUser().getId());
        if (!booking.getBooker().getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new PermissionException(bookingId);
        }

        return BookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByUser(Long userId, String state) {
        States currentState = States.valueOf(state);
        User findUser = findUserById(userId);
        Collection<Booking> bookingList = switch (currentState) {
            case ALL -> repository.findAllByBookerId(userId);
            case CURRENT -> repository.findAllCurrentBookingByBookerId(userId);
            case PAST -> repository.findAllPastBookingByBookerId(userId);
            case FUTURE -> repository.findAllFutureBookingByBookerId(userId);
            case WAITING -> repository.findAllByBookerIdAndStatus(userId, Statuses.WAITING);
            case REJECTED -> repository.findAllByBookerIdAndStatus(userId, Statuses.REJECTED);
        };

        return bookingList.stream()
                .map(BookingMapper::toDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByOwnerItems(Long userId, String state) {
        States currentState = States.valueOf(state);
        findUserById(userId);
        Collection<Booking> bookingList = switch (currentState) {
            case ALL -> repository.findAllByOwnerId(userId);
            case CURRENT -> repository.findAllCurrentBookingByOwnerId(userId);
            case PAST -> repository.findAllPastBookingByOwnerId(userId);
            case FUTURE -> repository.findAllFutureBookingByOwnerId(userId);
            case WAITING -> repository.findAllByOwnerIdAndStatus(userId, Statuses.WAITING);
            case REJECTED -> repository.findAllByOwnerIdAndStatus(userId, Statuses.REJECTED);
        };

        return bookingList.stream()
                .map(BookingMapper::toDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long bookingId) {
        Booking booking = findById(bookingId);
        repository.delete(booking);
    }

    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = findById(bookingId);
        Item item = findItemById(booking.getItem().getId());

        if (!item.getUser().getId().equals(userId)) {
            throw new PermissionException(userId);
        }

        if (!booking.getStatus().equals(Statuses.WAITING)) {
            throw new BookingStatusException(bookingId);
        }

        booking.setStatus(approved ? Statuses.APPROVED : Statuses.REJECTED);
        return BookingMapper.toDto(booking);
    }

    private Booking findById(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(Booking.class, bookingId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(User.class, userId));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(Item.class, itemId));
    }
}
