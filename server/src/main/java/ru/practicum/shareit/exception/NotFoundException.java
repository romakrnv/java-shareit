package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Class<?> className, Object id) {
        super(className.getSimpleName() + " with id " + id + " not found");
    }
}
