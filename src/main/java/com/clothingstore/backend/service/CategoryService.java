package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);
    Category update(Category category);
    void delete(String id);
    Category getById(String id);
    List<Category> getAll();
    List<Category> getByParentId(String parentId);
    boolean existsBySlug(String slug);
    boolean existsById(String id);
}