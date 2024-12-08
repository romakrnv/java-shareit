package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {
    @NotBlank
    String text;

    @Positive
    Long itemId;

    @Positive
    Long authorId;
}
