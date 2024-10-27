package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemDao {
    Item get(Long id);

    List<Item> findAllUserItems(Long id);

    Item create(Item item);

    Item update(Item item);

    List<Item> searchItems(String searchString);

    void delete(Long id);
}