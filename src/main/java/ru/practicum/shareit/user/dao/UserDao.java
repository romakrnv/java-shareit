package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {
    User get(Long id);

    List<User> findAll();

    User create(User user);

    User update(User user);

    void delete(Long id);

    boolean emailExist(String email);
}