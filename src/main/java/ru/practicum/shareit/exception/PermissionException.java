package ru.practicum.shareit.exception;

public class PermissionException extends RuntimeException {
    public PermissionException(Long id) {
        super(id + ": don't have permission to access this resource.");
    }
}
