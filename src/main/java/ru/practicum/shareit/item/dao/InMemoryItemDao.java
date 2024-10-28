package ru.practicum.shareit.item.dao;

import jakarta.validation.Valid;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> storage = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item get(Long id) {
        return storage.get(id);
    }

    @Override
    public List<Item> findAllUserItems(Long userId) {
        return storage.values().stream()
                .filter(item -> userId.equals(item.getOwner().getId())).toList();
    }

    @Override
    public Item create(@Valid Item item) {
        item.setId(getNewId());
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchItems(String searchString) {
        if (searchString.isEmpty()) {
            return List.of();
        }
        return storage.values().stream()
                .filter(item -> ((item.getName() != null && item.getName().toUpperCase().contains(searchString)) ||
                        (item.getDescription() != null && item.getDescription().toUpperCase().contains(searchString)))
                        && (item.getAvailable() != null && item.getAvailable()))
                .toList();
    }

    @Override
    public void delete(Long id) {
    }

    private Long getNewId() {
        return id++;
    }
}