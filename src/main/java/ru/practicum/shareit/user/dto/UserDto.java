package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email
    private String email;
}