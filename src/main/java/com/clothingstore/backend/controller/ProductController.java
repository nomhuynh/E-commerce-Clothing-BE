package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.product.ProductRequest;
import com.clothingstore.backend.dto.product.ProductResponse;
import com.clothingstore.backend.entity.Category;
import com.clothingstore.backend.entity.Material;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;
import com.clothingstore.backend.repository.CategoryRepository;
import com.clothingstore.backend.repository.MaterialRepository;
import com.clothingstore.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        Product product = toEntity(request);
        Product created = productService.create(product);
        return ResponseEntity.ok(ApiResponse.success("Product created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        Product product = toEntity(request);
        product.setId(id);
        Product updated = productService.update(product);
        return ResponseEntity.ok(ApiResponse.success("Product updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", toResponse(productService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
        List<ProductResponse> data = productService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Products fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(@RequestParam String keyword) {
        List<ProductResponse> data = productService.search(keyword).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Products found", data));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> filter(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String materialId,
            @RequestParam(required = false) ProductGender gender,
            @RequestParam(required = false) AgeGroup ageGroup,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String colorId,
            @RequestParam(required = false) String sizeId,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        List<ProductResponse> data = productService.filter(
            categoryId, materialId, gender, ageGroup, minPrice, maxPrice,
            colorId, sizeId, inStock, page, limit, sortBy, sortDir
        ).stream().map(this::toResponse).toList();
        
        return ResponseEntity.ok(ApiResponse.success("Products filtered", data));
    }

    private Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setGender(request.getGender());
        product.setAgeGroup(request.getAgeGroup());
        
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            product.setCategory(category);
        }
        
        if (request.getMaterialId() != null) {
            Material material = new Material();
            material.setId(request.getMaterialId());
            product.setMaterial(material);
        }
        
        return product;
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .materialId(product.getMaterial() != null ? product.getMaterial().getId() : null)
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .gender(product.getGender())
                .ageGroup(product.getAgeGroup())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}