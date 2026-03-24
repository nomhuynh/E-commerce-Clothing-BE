package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.category.CategoryRequest;
import com.clothingstore.backend.dto.category.CategoryResponse;
import com.clothingstore.backend.entity.Category;
import com.clothingstore.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        Category category = toEntity(request);
        Category created = categoryService.create(category);
        return ResponseEntity.ok(ApiResponse.success("Category created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable String id, @Valid @RequestBody CategoryRequest request) {
        Category category = toEntity(request);
        category.setId(id);
        Category updated = categoryService.update(category);
        return ResponseEntity.ok(ApiResponse.success("Category updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Category fetched", toResponse(categoryService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        List<CategoryResponse> data = categoryService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }

    private Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setImage(request.getImage());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        if (request.getParentId() != null) {
            Category parent = new Category();
            parent.setId(request.getParentId());
            category.setParent(parent);
        }
        
        return category;
    }

    private CategoryResponse toResponse(Category category) {
        List<CategoryResponse> children = category.getChildren() != null 
                ? category.getChildren().stream().map(this::toResponse).toList() 
                : null;
        
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .image(category.getImage())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .children(children)
                .build();
    }
}