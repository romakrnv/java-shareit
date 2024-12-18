package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.enums.Statuses;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.FullItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;

    private void createUserInDb() {
        Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
                "VALUES (:id , :name , :email);");
        userQuery.setParameter("id", "1");
        userQuery.setParameter("name", "Ivan Ivanov");
        userQuery.setParameter("email", "ivan@email");
        userQuery.executeUpdate();
    }

    private void createItemInDb() {
        Query itemQuery = em.createNativeQuery("INSERT INTO Items (id, name, description, available, owner_id, request_id) " +
                "VALUES (:id , :name , :description , :available , :owner_id , :request_id);");
        itemQuery.setParameter("id", "1");
        itemQuery.setParameter("name", "name");
        itemQuery.setParameter("description", "description");
        itemQuery.setParameter("available", Boolean.TRUE);
        itemQuery.setParameter("owner_id", "1");
        itemQuery.setParameter("request_id", "1");
        itemQuery.executeUpdate();
    }

    private void createLastBookingInDb() {
        Query lastBookingQuery = em.createNativeQuery("INSERT INTO Bookings (id, start_date, end_date, item_id, status, booker_id) " +
                "VALUES (:id , :startDate , :eneDate , :itemId , :status , :bookerId);");
        lastBookingQuery.setParameter("id", "1");
        lastBookingQuery.setParameter("startDate", LocalDateTime.of(2024, 7, 1, 19, 30, 15));
        lastBookingQuery.setParameter("eneDate", LocalDateTime.of(2024, 7, 2, 19, 30, 15));
        lastBookingQuery.setParameter("itemId", 1L);
        lastBookingQuery.setParameter("status", Statuses.APPROVED);
        lastBookingQuery.setParameter("bookerId", 1L);
        lastBookingQuery.executeUpdate();
    }

    private void createNextBookingInDb() {
        Query nextBookingQuery = em.createNativeQuery("INSERT INTO Bookings (id, start_date, end_date, item_id, status, booker_id) " +
                "VALUES (:id , :startDate , :eneDate , :itemId , :status , :bookerId);");
        nextBookingQuery.setParameter("id", "2");
        nextBookingQuery.setParameter("startDate", LocalDateTime.of(2024, 12, 1, 19, 30, 15));
        nextBookingQuery.setParameter("eneDate", LocalDateTime.of(2024, 12, 2, 19, 30, 15));
        nextBookingQuery.setParameter("itemId", 1L);
        nextBookingQuery.setParameter("status", Statuses.APPROVED);
        nextBookingQuery.setParameter("bookerId", 1L);
        nextBookingQuery.executeUpdate();
    }

    private void createCommentInDb() {
        Query commentQuery = em.createNativeQuery("INSERT INTO Comments (id, text, item_id, author_id, created) " +
                "VALUES (:id , :text , :item_id , :author_id , :created);");
        commentQuery.setParameter("id", "1");
        commentQuery.setParameter("text", "text");
        commentQuery.setParameter("item_id", 1L);
        commentQuery.setParameter("author_id", 1L);
        commentQuery.setParameter("created", LocalDateTime.of(2024, 8, 2, 19, 30, 15));
        commentQuery.executeUpdate();
    }

    @Test
    void createItemTest() {
        createUserInDb();

        ItemDto newItem = ItemDto.builder().name("name").description("description").available(true).ownerId(1L).requestId(1L).build();

        ItemDto findItem = itemService.create(1L, newItem);

        assertThat(findItem.getId(), CoreMatchers.notNullValue());
        assertThat(findItem.getName(), Matchers.equalTo(newItem.getName()));
        assertThat(findItem.getDescription(), Matchers.equalTo(newItem.getDescription()));
        assertThat(findItem.getAvailable(), Matchers.equalTo(newItem.getAvailable()));
        assertThat(findItem.getOwnerId(), CoreMatchers.notNullValue());
        assertThat(findItem.getRequestId(), CoreMatchers.notNullValue());
    }

    @Test
    void getItemTest() {
        createUserInDb();
        createItemInDb();
        createLastBookingInDb();
        createNextBookingInDb();
        createCommentInDb();

        FullItemDto loadItem = itemService.findItem(1L, 1L);

        assertThat(loadItem.getId(), CoreMatchers.notNullValue());
        assertThat(loadItem.getName(), Matchers.equalTo("name"));
        assertThat(loadItem.getDescription(), Matchers.equalTo("description"));
        assertThat(String.valueOf(loadItem.getAvailable()), true);
        assertThat(loadItem.getComments(), CoreMatchers.notNullValue());
        assertThat(loadItem.getOwnerId(), CoreMatchers.notNullValue());
        assertThat(loadItem.getRequestId(), CoreMatchers.notNullValue());
    }

    @Test
    void getItemForAnotherUserTest() {
        createUserInDb();
        createItemInDb();
        createLastBookingInDb();
        createNextBookingInDb();
        createCommentInDb();

        FullItemDto loadItem = itemService.findItem(999L, 1L);

        assertThat(loadItem.getId(), CoreMatchers.notNullValue());
        assertThat(loadItem.getName(), Matchers.equalTo("name"));
        assertThat(loadItem.getDescription(), Matchers.equalTo("description"));
        assertThat(String.valueOf(loadItem.getAvailable()), true);
        assertThat(loadItem.getLastBooking(), CoreMatchers.nullValue());
        assertThat(loadItem.getNextBooking(), CoreMatchers.nullValue());
        assertThat(loadItem.getComments(), CoreMatchers.notNullValue());
        assertThat(loadItem.getOwnerId(), CoreMatchers.notNullValue());
        assertThat(loadItem.getRequestId(), CoreMatchers.notNullValue());
    }

    @Test
    void testFindAll() {
        createUserInDb();

        List<ItemDto> items = List.of(
                makeNewItemRequest("name1", "description1", Boolean.TRUE, 1L, 1L),
                makeNewItemRequest("name2", "description2", Boolean.TRUE, 1L, 2L),
                makeNewItemRequest("name3", "description3", Boolean.TRUE, 1L, 3L)
        );

        for (ItemDto itemRequest : items) {
            itemService.create(1L, itemRequest);
        }

        Collection<FullItemDto> loadRequests = itemService.findAll(1L);

        assertThat(loadRequests, hasSize(items.size()));
        for (ItemDto item : items) {
            assertThat(loadRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("lastBooking", nullValue()),
                    hasProperty("nextBooking", nullValue()),
                    hasProperty("comments", CoreMatchers.equalTo(new ArrayList<>())),
                    hasProperty("ownerId", equalTo(item.getOwnerId())),
                    hasProperty("requestId", equalTo(item.getRequestId()))
            )));
        }
    }

    @Test
    void testFindItemsForTenant() {
        createUserInDb();

        List<ItemDto> items = List.of(
                makeNewItemRequest("name1", "description1", Boolean.TRUE, 1L, 1L),
                makeNewItemRequest("name2", "description2", Boolean.TRUE, 1L, 2L),
                makeNewItemRequest("name3", "description3", Boolean.TRUE, 1L, 3L)
        );

        for (ItemDto itemRequest : items) {
            itemService.create(1L, itemRequest);
        }

        Collection<ItemDto> loadRequests = itemService.findItemsForTenant(1L, "cript");

        assertThat(loadRequests, hasSize(items.size()));
        for (ItemDto item : items) {
            assertThat(loadRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("ownerId", equalTo(item.getOwnerId())),
                    hasProperty("requestId", equalTo(item.getRequestId()))
            )));
        }
    }

    @Test
    void updateItemTest() {
        createUserInDb();
        createItemInDb();

        ItemDto updItemRequest = new ItemDto(1L, "name1", "description1", Boolean.FALSE, 1L, 2L);
        ItemDto findItemRequest = itemService.update(1L, updItemRequest, 1L);

        MatcherAssert.assertThat(findItemRequest.getId(), CoreMatchers.equalTo(updItemRequest.getId()));
        MatcherAssert.assertThat(findItemRequest.getName(), Matchers.equalTo(updItemRequest.getName()));
        MatcherAssert.assertThat(findItemRequest.getDescription(), Matchers.equalTo(updItemRequest.getDescription()));
        MatcherAssert.assertThat(findItemRequest.getAvailable(), Matchers.equalTo(updItemRequest.getAvailable()));
        MatcherAssert.assertThat(findItemRequest.getOwnerId(), Matchers.equalTo(updItemRequest.getOwnerId()));
    }

    @Test
    void deleteItemTest() {
        createUserInDb();
        createItemInDb();

        itemService.delete(1L, 1L);

        TypedQuery<Item> selectQuery = em.createQuery("Select i from Item i where i.description like :description", Item.class);
        List<Item> users = selectQuery.setParameter("description", "description1").getResultList();

        MatcherAssert.assertThat(users, CoreMatchers.equalTo(new ArrayList<>()));
    }

    @Test
    void addCommentTest() {
        createUserInDb();
        createItemInDb();
        createLastBookingInDb();
        createNextBookingInDb();

        NewCommentDto newComment = new NewCommentDto();
        newComment.setText("comment");
        newComment.setAuthorId(1L);
        newComment.setItemId(1L);
        CommentDto findComment = itemService.addComment(1L, 1L, newComment);

        MatcherAssert.assertThat(findComment.getId(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(findComment.getText(), Matchers.equalTo(newComment.getText()));
        MatcherAssert.assertThat(findComment.getItemId(), Matchers.equalTo(newComment.getItemId()));
        MatcherAssert.assertThat(findComment.getAuthorName(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(findComment.getCreated(), CoreMatchers.notNullValue());
    }

    private ItemDto makeNewItemRequest(String name, String description, Boolean available, Long ownerId, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setOwnerId(ownerId);
        dto.setRequestId(requestId);

        return dto;
    }
}
