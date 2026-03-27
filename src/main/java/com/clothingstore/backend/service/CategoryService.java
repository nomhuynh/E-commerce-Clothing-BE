package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);
    Category update(Category category);
    Category getById(String id);
    Category getBySlug(String slug);
    List<Category> getAll();
    void delete(String id);
}
