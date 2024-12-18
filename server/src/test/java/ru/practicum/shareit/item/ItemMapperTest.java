package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemMapperTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nextDay = LocalDateTime.now().plusDays(1);

    private final User user = new User(1L, "John Doe", "john.doe@mail.com");

    private final CommentDto commentDto = new CommentDto(1L, "text", 1L, "John Doe", nextDay);
    private final List<CommentDto> comments = List.of(commentDto);

//    private final ItemDto newItem = new ItemDto("name", "description", Boolean.TRUE, 1L, 1L);
    private final ItemDto updItem = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);
    private final ItemDto updEmptyItem = new ItemDto(1L, "", "", null, 1L, 1L);
    private final ItemDto dto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

    private final FullItemDto advItemDto = new FullItemDto(1L, "name", "description", Boolean.TRUE, now, nextDay, comments, 1L, 1L);
    private final FullItemDto advItemDtoNullDates = new FullItemDto(1L, "name", "description", Boolean.TRUE, null, null, comments, 1L, 1L);

    private final Item item = new Item(1L, "name", "description", Boolean.TRUE, user, 1L);
    private final Comment comment = new Comment(1L, "text", item, user, nextDay);

    private final Item itemNoRequest = new Item(1L, "name", "description", Boolean.TRUE, user, null);
    private final FullItemDto fullItemDto = new FullItemDto(1L, "name", "description", Boolean.TRUE, now, nextDay, comments, 1L, null);
    private final FullItemDto fullItemDtoNullDatesNoRequest = new FullItemDto(1L, "name", "description", Boolean.TRUE, null, null, comments, 1L, null);
    private final ItemDto dtoNoRequest = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, null);

    @Test
    public void toItemDtoTest() {
        ItemDto itemDto = ItemMapper.toDto(item);
        assertThat(itemDto, equalTo(dto));
    }

    @Test
    public void toItemDtoWithOutRequestTest() {
        ItemDto itemDto = ItemMapper.toDto(itemNoRequest);
        assertThat(itemDto, equalTo(dtoNoRequest));
    }

    @Test
    public void toAdvancedItemDtoTest() {
        FullItemDto advDto = ItemMapper.mapToFullItemDto(item, List.of(comment));
        assertThat(advDto, equalTo(advItemDtoNullDates));
    }

    @Test
    public void toAdvancedItemDtoWithOutRequestTest() {
        FullItemDto advDto = ItemMapper.mapToFullItemDto(itemNoRequest, List.of(comment));
        assertThat(advDto, equalTo(fullItemDtoNullDatesNoRequest));
    }

    @Test
    public void toAdvancedItemDtoWithDatesTest() {
        FullItemDto advDto = ItemMapper.mapToFullItemDto(item, List.of(comment), Optional.of(now), Optional.of(nextDay));
        assertThat(advDto, equalTo(advItemDto));
    }

    @Test
    public void toAdvancedItemDtoWithDatesWithOutRequestTest() {
        FullItemDto advDto = ItemMapper.mapToFullItemDto(itemNoRequest, List.of(comment), Optional.of(now), Optional.of(nextDay));
        assertThat(advDto, equalTo(fullItemDto));
    }

    @Test
    public void toItemTest() {
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item i = ItemMapper.toEntity(user, newItem);
        assertThat(i.getName(), equalTo(item.getName()));
        assertThat(i.getDescription(), equalTo(item.getDescription()));
        assertThat(i.getAvailable(), equalTo(item.getAvailable()));
        assertThat(i.getUser(), equalTo(user));
        assertThat(i.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    public void toItemTestWithOutRequestTest() {
        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();
        Item i = ItemMapper.toEntity(user, newItem);
        assertThat(i.getName(), equalTo(item.getName()));
        assertThat(i.getDescription(), equalTo(item.getDescription()));
        assertThat(i.getAvailable(), equalTo(item.getAvailable()));
        assertThat(i.getUser(), equalTo(user));
        assertThat(i.getRequestId(), equalTo(1L));
    }

    @Test
    public void updateItemFieldsTest() {
        Item i = ItemMapper.update(item, updItem);
        assertThat(i.getName(), equalTo(item.getName()));
        assertThat(i.getDescription(), equalTo(item.getDescription()));
        assertThat(i.getAvailable(), equalTo(item.getAvailable()));
        assertThat(i.getUser(), equalTo(user));
        assertThat(i.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    public void updateItemEmptyFieldsTest() {
        Item i = ItemMapper.update(item, updEmptyItem);
        assertThat(i.getName(), equalTo(item.getName()));
        assertThat(i.getDescription(), equalTo(item.getDescription()));
        assertThat(i.getAvailable(), equalTo(item.getAvailable()));
        assertThat(i.getUser(), equalTo(user));
        assertThat(i.getRequestId(), equalTo(item.getRequestId()));
    }
}
