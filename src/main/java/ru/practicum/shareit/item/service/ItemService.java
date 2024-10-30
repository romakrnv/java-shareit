package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.ecxeptions.ItemNotFoundException;
import ru.practicum.shareit.item.ecxeptions.PermissionItemException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

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

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = UserMapper.toEntity(userService.getUser(userId));
        if (user == null) {
            throw new ItemNotFoundException(userId);
        }
        Item item = ItemMapper.toEntity(itemDto);
        item.setOwner(user);
        return ItemMapper.toDto(itemDao.create(item));
    }

    public ItemDto updateItem(Long id, ItemDto itemDto, Long userId) {
        Item existedItem = itemDao.get(id);
        if (existedItem == null) {
            throw new ItemNotFoundException(id);
        }
        if (!existedItem.getOwner().getId().equals(userId)) {
            throw new PermissionItemException(userId);
        }
        if (itemDto.getName() != null) {
            existedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existedItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toDto(itemDao.update(existedItem));
    }

    public List<ItemDto> searchItems(String searchString) {
        return itemDao.searchItems(searchString).stream().map(ItemMapper::toDto).toList();
    }
}