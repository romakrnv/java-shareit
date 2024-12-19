package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ItemService {
    final ItemRepository repository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;

    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User findUser = findUserById(ownerId);
        Item item = ItemMapper.toEntity(findUser, itemDto);
        item = repository.save(item);

        return ItemMapper.toDto(item);
    }

    @Transactional(readOnly = true)
    public FullItemDto findItem(Long ownerId, Long itemId) {
        Item item = findById(itemId);

        if (item.getUser().getId().equals(ownerId)) {
            return ItemMapper.mapToFullItemDto(findById(itemId),
                    commentRepository.findAllByItemId(itemId),
                    getLastBookingEndDate(itemId),
                    getNextBookingStartDate(itemId));
        }

        return ItemMapper.mapToFullItemDto(findById(itemId), commentRepository.findAllByItemId(itemId));
    }

    @Transactional(readOnly = true)
    public Collection<FullItemDto> findAll(Long ownerId) {
        List<Item> userItems = repository.findAllByUserId(ownerId);

        if (!userItems.isEmpty()) {
            return fillItemData(userItems);
        }

        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public Collection<ItemDto> findItemsForTenant(Long ownerId, String text) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }

        return repository.findItemsForTenant(text).stream()
                .map(ItemMapper::toDto)
                .collect(toList());
    }

    @Transactional
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        Item item = findById(itemId);

        if (!item.getUser().getId().equals(ownerId)) {
            throw new PermissionException(ownerId);
        }
        Item updatedItem = repository.save(ItemMapper.update(item, itemDto));
        return ItemMapper.toDto(updatedItem);
    }

    @Transactional
    public void delete(Long ownerId, Long itemId) {
        Item item = findById(itemId);
        User findUser = findUserById(ownerId);

        if (!findUser.getId().equals(ownerId)) {
            throw new PermissionException(ownerId);
        }
        repository.delete(item);
    }

    @Transactional
    public CommentDto addComment(Long itemId, Long userId, NewCommentDto request) {
        User findUser = findUserById(userId);
        Item findItem = findById(itemId);

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException(String.format("User %s cannot create comment , " +
                    "because did not use the item %s", findUser.getId(), findItem.getName()));
        }

        Comment comment = CommentMapper.toEntity(findUser, findItem, request);
        comment = commentRepository.save(comment);

        return CommentMapper.toDto(comment);
    }

    private Item findById(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(Item.class, itemId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(User.class, userId));
    }

    private Optional<LocalDateTime> getLastBookingEndDate(Long itemId) {
        return bookingRepository.findLastBookingEndByItemId(itemId)
                .stream()
                .max(Comparator.naturalOrder());
    }

    private Optional<LocalDateTime> getNextBookingStartDate(Long itemId) {
        return bookingRepository.findNextBookingStartByItemId(itemId)
                .stream()
                .min(Comparator.naturalOrder());
    }

    private List<FullItemDto> fillItemData(List<Item> userItems) {
        List<Long> itemIds = userItems.stream()
                .map(Item::getId)
                .toList();

        Map<Long, Booking> lastBookings = bookingRepository
                .findLastBookingsByItemIds(itemIds)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));

        Map<Long, Booking> nextBookings = bookingRepository
                .findNextBookingsByItemIds(itemIds)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));

        Map<Long, List<Comment>> commentsByItemId = commentRepository
                .findByItemIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return userItems.stream()
                .map(item -> {
                    Long itemId = item.getId();
                    Booking lastBooking = lastBookings.get(itemId);
                    Booking nextBooking = nextBookings.get(itemId);
                    List<Comment> comments = commentsByItemId.getOrDefault(itemId, Collections.emptyList());

                    return ItemMapper.mapToFullItemDto(
                            item,
                            comments,
                            Optional.ofNullable(lastBooking).map(Booking::getEnd),
                            Optional.ofNullable(nextBooking).map(Booking::getStart)
                    );
                })
                .toList();
    }
}