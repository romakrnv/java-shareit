package ru.practicum.shareit.item.ecxeptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("Item with id: " + id + " not found");
    }
}