package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.product.ProductImageResponse;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.ProductImage;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService imageService;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getByProduct(@PathVariable String productId) {
        List<ProductImageResponse> data = imageService.getByProductId(productId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Images fetched", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductImageResponse>> create(
            @PathVariable String productId,
            @RequestBody ProductImageResponse request) {
        
        ProductImage image = toEntity(request, productId);
        ProductImage created = imageService.create(image);
        return ResponseEntity.ok(ApiResponse.success("Image created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductImageResponse>> update(
            @PathVariable String productId,
            @PathVariable String id,
            @RequestBody ProductImageResponse request) {
        
        ProductImage image = toEntity(request, productId);
        image.setId(id);
        ProductImage updated = imageService.update(image);
        return ResponseEntity.ok(ApiResponse.success("Image updated", toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String productId,
            @PathVariable String id) {
        imageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Image deleted", null));
    }

    @PatchMapping("/{id}/thumbnail")
    public ResponseEntity<ApiResponse<ProductImageResponse>> setThumbnail(
            @PathVariable String productId,
            @PathVariable String id) {
        
        ProductImage updated = imageService.setThumbnail(id);
        return ResponseEntity.ok(ApiResponse.success("Thumbnail set", toResponse(updated)));
    }

    @PutMapping("/reorder")
    public ResponseEntity<ApiResponse<Void>> reorder(
            @PathVariable String productId,
            @RequestBody List<String> imageIds) {
        imageService.reorder(imageIds);
        return ResponseEntity.ok(ApiResponse.success("Images reordered", null));
    }

    private ProductImage toEntity(ProductImageResponse request, String productId) {
        ProductImage image = new ProductImage();
        
        if (productId != null) {
            Product product = new Product();
            product.setId(productId);
            image.setProduct(product);
        }
        
        image.setImageUrl(request.getImageUrl());
        image.setSortOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        image.setIsThumbnail(request.getIsThumbnail() != null ? request.getIsThumbnail() : false);
        
        return image;
    }

    private ProductImageResponse toResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct() != null ? image.getProduct().getId() : null)
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getSortOrder())
                .isThumbnail(image.getIsThumbnail())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }
}