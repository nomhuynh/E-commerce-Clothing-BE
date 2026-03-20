package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Category;
import com.clothingstore.backend.repository.CategoryRepository;
import com.clothingstore.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category create(Category category) {
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new RuntimeException("Category slug already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category) {
        if (category.getId() == null) {
            throw new RuntimeException("Category id is required for update");
        }
        if (!categoryRepository.existsById(category.getId())) {
            throw new RuntimeException("Category not found");
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category getById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public Category getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
