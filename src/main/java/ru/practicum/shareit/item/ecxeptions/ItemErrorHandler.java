package ru.practicum.shareit.item.ecxeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

@RestControllerAdvice(basePackages = "ru.practicum.shareit.item")
public class ItemErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ItemErrorResponse handleItemNotFound(final ItemNotFoundException e) {
        return new ItemErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ItemErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ItemErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ItemErrorResponse handlePermission(final PermissionItemException e) {
        return new ItemErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ItemErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ItemErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ItemErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        return new ItemErrorResponse(e.getMessage());
    }
}