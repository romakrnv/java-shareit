package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final String urlTemplate = "/items";
    private final String headerUserId = "X-Sharer-User-Id";

    private ItemDto makeItemDto(Long id, String name, String description, Boolean available, Long ownerId, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setOwnerId(ownerId);
        dto.setRequestId(requestId);

        return dto;
    }

    private FullItemDto makeAdvancedItemDto(Long id, String name, String description, Boolean available, LocalDateTime lastBooking,
                                            LocalDateTime nextBooking, List<CommentDto> comments, Long ownerId, Long requestId) {
        FullItemDto dto = new FullItemDto();

        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        dto.setOwnerId(ownerId);
        dto.setRequestId(requestId);

        return dto;
    }

    private CommentDto makeCommentDto(Long id, String text, Long ownerId, String authorName, LocalDateTime created) {
        CommentDto dto = new CommentDto();
        dto.setId(id);
        dto.setText(text);
        dto.setItemId(ownerId);
        dto.setAuthorName(authorName);
        dto.setCreated(created);

        return dto;
    }

    @Test
    void createTest() throws Exception {
        ItemDto requestDto = makeItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

        when(itemService.create(anyLong(), any())).thenReturn(requestDto);

        mvc.perform(post(urlTemplate)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserId, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @Test
    void findItemTest() throws Exception {
        CommentDto comment = makeCommentDto(1L, "text", 1L, "authorName", LocalDateTime.of(2022, 7, 3, 19, 30, 1));
        FullItemDto requestDto = makeAdvancedItemDto(1L, "name", "description", true, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 4, 19, 30, 1), List.of(comment), 1L, 1L);

        when(itemService.findItem(anyLong(), anyLong())).thenReturn(requestDto);

        mvc.perform(get(urlTemplate + "/" + requestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)))
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments[0].id").value(is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text").value(is(comment.getText()), String.class))
                .andExpect(jsonPath("$.comments[0].authorName").value(is(comment.getAuthorName()), String.class));
    }

    @Test
    void findAllTest() throws Exception {
        CommentDto comment = makeCommentDto(1L, "text", 1L, "authorName", LocalDateTime.of(2022, 7, 3, 19, 30, 1));
        FullItemDto requestDto1 = makeAdvancedItemDto(1L, "name1", "description1", true, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 4, 19, 30, 1), List.of(comment), 1L, 1L);
        FullItemDto requestDto2 = makeAdvancedItemDto(1L, "name2", "description2", true, LocalDateTime.of(2023, 7, 3, 19, 30, 1),
                LocalDateTime.of(2023, 7, 4, 19, 30, 1), List.of(comment), 1L, 1L);

        List<FullItemDto> newRequests = List.of(requestDto1, requestDto2);

        when(itemService.findAll(anyLong())).thenReturn(newRequests);

        mvc.perform(get(urlTemplate)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(is(newRequests.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[1].id").value(is(newRequests.getLast().getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(newRequests)));
    }

    @Test
    void findItemsForTenantTest() throws Exception {
        ItemDto requestDto1 = makeItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);
        ItemDto requestDto2 = makeItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

        List<ItemDto> newRequests = List.of(requestDto1, requestDto2);

        when(itemService.findItemsForTenant(anyLong(), anyString())).thenReturn(newRequests);

        mvc.perform(get(urlTemplate + "/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(is(newRequests.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[1].id").value(is(newRequests.getLast().getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(newRequests)));
    }

    @Test
    void updateTest() throws Exception {
        ItemDto requestDto = makeItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

        when(itemService.update(anyLong(), any(), anyLong())).thenReturn(requestDto);

        mvc.perform(patch(urlTemplate + "/" + requestDto.getId())
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete(urlTemplate + "/" + anyLong())
                        .header(headerUserId, anyLong()))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(anyLong(), anyLong());
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDto comment = makeCommentDto(1L, "text", 1L, "authorName", LocalDateTime.of(2022, 7, 3, 19, 30, 1));

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(post(urlTemplate + "/" + comment.getId() + "/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(comment)));
    }

}
