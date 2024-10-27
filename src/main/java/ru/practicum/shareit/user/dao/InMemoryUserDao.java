package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserDao implements UserDao {
    Map<Long, User> storage = new HashMap<>();
    private Long id = 1L;

    @Override
    public User get(Long id) {
        return storage.get(id);
    }

    @Override
    public List<User> findAll() {
        return storage.values().stream().toList();
    }

    @Override
    public User create(User user) {
        user.setId(getNewId());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean emailExist(String email) {
        return storage.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private Long getNewId() {
        return id++;
    }
}