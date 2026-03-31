package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.entity.Category;
import com.clothingstore.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categoryService.getAll()));
    }

    @Operation(summary = "Get category by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Category fetched", categoryService.getById(id)));
    }

    @Operation(summary = "Create category")
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody Category category) {
        return ResponseEntity.ok(ApiResponse.success("Category created", categoryService.create(category)));
    }

    @Operation(summary = "Update category")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> update(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryService.update(category)));
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }
}
