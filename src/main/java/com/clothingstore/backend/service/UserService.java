package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.entity.enums.Role;
import com.clothingstore.backend.entity.enums.UserStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    User create(User user);
    User update(User user);
    User getById(String id);
    User getByEmail(String email);
    List<User> getAll();

    /** Admin: phân trang + tìm theo email/tên + lọc role/status */
    Page<User> findPageForAdmin(int page, int limit, String search, Role role, UserStatus status);

    void delete(String id);
}
