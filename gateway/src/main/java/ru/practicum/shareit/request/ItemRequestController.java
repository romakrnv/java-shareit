package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    private final String headerUserId = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItemRequest(@RequestHeader(headerUserId) Long userId,
                                                 @Valid @RequestBody NewRequest itemRequest) {
        return itemRequestClient.addItemRequest(userId, itemRequest);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequest(@PathVariable Long requestId) {
        return itemRequestClient.getItemRequest(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequestorId(@RequestHeader(headerUserId) Long requestorId) {
        return itemRequestClient.getItemRequests(null, requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOfAnotherRequestors(@RequestHeader(headerUserId) Long requestorId) {
        return itemRequestClient.getItemRequests("/all", requestorId);
    }

    @PutMapping
    public ResponseEntity<Object> updateItemRequest(@RequestHeader(headerUserId) Long userId,
                                                    @Valid @RequestBody UpdateRequest newItemRequest) {
        return itemRequestClient.updateItemRequest(userId, newItemRequest);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> deleteItemRequest(@PathVariable Long requestId) {
        return itemRequestClient.deleteItemRequest(requestId);
    }
}