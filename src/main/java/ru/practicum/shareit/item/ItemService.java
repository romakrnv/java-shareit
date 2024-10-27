package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.ecxeptions.ItemNotFoundException;
import ru.practicum.shareit.item.ecxeptions.PermissionItemException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemDao;
    private final UserService userService;

    public ItemDto getItem(Long id) {
        ItemDto item = ItemMapper.toDto(itemDao.get(id));
        if (item == null) {
            throw new ItemNotFoundException(id);
        }
        return item;
    }

    public List<ItemDto> getAllUserItems(Long userId) {
        return itemDao.findAllUserItems(userId).stream().map(ItemMapper::toDto).toList();
    }

    public ItemDto createItem(Item item, Long userId) {
        User user = UserMapper.toEntity(userService.getUser(userId));
        if (user == null) {
            throw new ItemNotFoundException(userId);
        }
        item.setOwner(user);
        return ItemMapper.toDto(itemDao.create(item));
    }

    public ItemDto updateItem(Long id, Map<String, Object> updates, Long userId) {
        Item item = itemDao.get(id);
        if (item == null) {
            throw new ItemNotFoundException(id);
        }
        if (!item.getOwner().getId().equals(userId)) {
            throw new PermissionItemException(userId);
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    item.setName((String) value);
                    break;
                case "description":
                    item.setDescription((String) value);
                    break;
                case "available":
                    item.setAvailable((Boolean) value);
                    break;
            }
        });
        return ItemMapper.toDto(itemDao.update(item));
    }

    public List<ItemDto> searchItems(String searchString) {
        return itemDao.searchItems(searchString).stream().map(ItemMapper::toDto).toList();
    }
}