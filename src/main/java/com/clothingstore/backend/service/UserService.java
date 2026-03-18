package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.User;

import java.util.List;

public interface UserService {
    User create(User user);
    User update(User user);
    User getById(String id);
    User getByEmail(String email);
    List<User> getAll();
    void delete(String id);
}
