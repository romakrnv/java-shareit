package ru.practicum.shareit.item.dto;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getUser().getId());

        return dto;
    }

    public static FullItemDto mapToFullItemDto(Item item,
                                               List<Comment> comments,
                                               Optional<LocalDateTime> lastBooking,
                                               Optional<LocalDateTime> nextBooking) {
        FullItemDto dto = new FullItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getUser().getId());
        lastBooking.ifPresent(dto::setLastBooking);
        nextBooking.ifPresent(dto::setNextBooking);
        dto.setComments(comments.stream().map(CommentMapper::toDto).toList());

        return dto;
    }

    public static FullItemDto mapToFullItemDto(Item item, List<Comment> comments) {
        FullItemDto dto = new FullItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getUser().getId());
        dto.setComments(comments.stream().map(CommentMapper::toDto).toList());


        return dto;
    }

    public static Item toEntity(User owner, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUser(owner);

        return item;
    }

    public static Item update(Item item, ItemDto itemDto) {
        if (!StringUtils.isBlank(itemDto.getName())) {
            item.setName(itemDto.getName());
        }

        if (!StringUtils.isBlank(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }
}