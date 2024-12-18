package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestService {
    RequestRepository requestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Autowired
    public ItemRequestService(RequestRepository requestRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private ItemRequest findById(Long itemRequestId) {
        return requestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(ItemRequest.class, itemRequestId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(User.class, userId));
    }

    private List<ItemRequestDto> fillRequestsData(List<ItemRequest> requests) {

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<Item>> requestItems = itemRepository
                .findByRequestIdIn(requestIds)
                .stream()
                .collect(groupingBy(Item::getRequestId, toList()));

        List<ItemRequestDto> requestsList = new ArrayList<>();
        for (ItemRequest request : requests) {

            requestsList.add(ItemRequestMapper.mapToItemRequestDto(request,
                    requestItems.getOrDefault(request.getId(), Collections.emptyList()))
            );
        }

        return requestsList;
    }

    @Transactional
    public ItemRequestDto create(Long userId, NewRequest request) {
        User findUser = findUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(request, findUser, LocalDateTime.now());
        itemRequest = requestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    public ItemRequestDto findItemRequest(Long itemRequestId) {
        ItemRequest itemRequest = findById(itemRequestId);

        Collection<Item> items = itemRepository.findByRequestId(itemRequestId);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> findAllByRequestorId(Long requestorId) {
        User findUser = findUserById(requestorId);

        List<ItemRequest> requests = requestRepository.findByRequestorId(requestorId);

        return fillRequestsData(requests)
                .stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> findAllOfAnotherRequestors(Long requestorId) {
        return requestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemRequestDto update(Long userId, UpdateRequest request) {
        User findUser = findUserById(userId);

        if (request.getId() == null) {
            throw new ValidationException("request id should be specified");
        }

        ItemRequest updatedItem = ItemRequestMapper.updateItemFields(findById(request.getId()), request, findUser);
        updatedItem = requestRepository.save(updatedItem);

        return ItemRequestMapper.mapToItemRequestDto(updatedItem);
    }

    @Transactional
    public void delete(Long itemRequestId) {
        ItemRequest itemRequest = findById(itemRequestId);
        requestRepository.delete(itemRequest);
    }
}