package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Category;
import com.clothingstore.backend.repository.CategoryRepository;
import com.clothingstore.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category create(Category category) {
        if (category.getParent() != null && category.getParent().getId() != null) {
            Category parent = categoryRepository.findById(category.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Category category) {
        Category existing = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (category.getName() != null) {
            existing.setName(category.getName());
        }
        if (category.getSlug() != null) {
            existing.setSlug(category.getSlug());
        }
        if (category.getImage() != null) {
            existing.setImage(category.getImage());
        }
        if (category.getIsActive() != null) {
            existing.setIsActive(category.getIsActive());
        }
        if (category.getParent() != null && category.getParent().getId() != null) {
            if (category.getParent().getId().equals(category.getId())) {
                throw new RuntimeException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(category.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            existing.setParent(parent);
        }
        
        return categoryRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check if category has children
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("Cannot delete category with subcategories");
        }
        
        categoryRepository.delete(category);
    }

    @Override
    public Category getById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getByParentId(String parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsById(String id) {
        return categoryRepository.existsById(id);
    }
}