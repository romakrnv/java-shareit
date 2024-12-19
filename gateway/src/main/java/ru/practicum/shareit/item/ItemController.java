package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    private final String headerUserId = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(@RequestHeader(headerUserId) Long ownerId,
                                          @Valid @RequestBody NewItemRequest item) {
        return itemClient.addItem(ownerId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader(headerUserId) Long ownerId,
                                           @PathVariable Long itemId) {
        return itemClient.getItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsForTenant(@RequestHeader(headerUserId) Long ownerId,
                                                     @RequestParam(name = "text", defaultValue = "") String text) {
        return itemClient.getItems("/search", ownerId, text);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(headerUserId) Long ownerId) {
        return itemClient.getItems(null, ownerId, null);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @Valid @RequestBody UpdateItemRequest newItem,
                                             @RequestHeader(headerUserId) Long ownerId) {
        return itemClient.updateItem(itemId, ownerId, newItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(headerUserId) Long ownerId,
                                             @PathVariable Long itemId) {
        return itemClient.deleteItem(itemId, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader(headerUserId) Long userId,
                                             @Valid @RequestBody NewCommentRequest comment) {
        return itemClient.addComment("/" + itemId + "/comment", userId, comment);
    }
}