package ru.practicum.shareit.user.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("email: '" + email + "' already exist");
    }
}