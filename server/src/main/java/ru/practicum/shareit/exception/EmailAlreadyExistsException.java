package ru.practicum.shareit.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("email: '" + email + "' already exist");
    }
}