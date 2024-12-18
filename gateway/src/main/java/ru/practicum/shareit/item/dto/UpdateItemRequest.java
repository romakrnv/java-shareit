package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class UpdateItemRequest {
    Long id;
    String name;
    String description;
    Boolean available;
    @Positive
    Long ownerId;
    @Positive
    Long requestId;
}
