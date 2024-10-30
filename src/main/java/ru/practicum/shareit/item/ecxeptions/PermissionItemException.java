package ru.practicum.shareit.item.ecxeptions;

public class PermissionItemException extends RuntimeException {
    public PermissionItemException(Long userId) {
        super("User with id :" + userId + " don't have permission to access this resource");
    }
}