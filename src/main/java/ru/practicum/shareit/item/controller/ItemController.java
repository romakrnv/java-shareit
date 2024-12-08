package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String id = "/{item-id}";
    private final String search = "/search";
    private final String comment = "/comment";
    private final String itemComment = id + comment;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody ItemDto item) {
        return itemService.create(ownerId, item);
    }

    @GetMapping(id)
    public FullItemDto findItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                @PathVariable("item-id") Long itemId) {
        return itemService.findItem(ownerId, itemId);
    }

    @GetMapping(search)
    public Collection<ItemDto> findItemsForTenant(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.findItemsForTenant(ownerId, text);
    }

    @GetMapping
    public Collection<FullItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.findAll(ownerId);
    }

    @PatchMapping(id)
    public ItemDto update(@PathVariable("item-id") Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @DeleteMapping(id)
    public void delete(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @PathVariable("item-id") Long itemId) {
        itemService.delete(ownerId, itemId);
    }

    @PostMapping(itemComment)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable("item-id") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody NewCommentDto comment) {
        return itemService.addComment(itemId, userId, comment);
    }
}