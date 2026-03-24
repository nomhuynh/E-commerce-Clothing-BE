package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.product.ProductVariantResponse;
import com.clothingstore.backend.entity.Color;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.ProductVariant;
import com.clothingstore.backend.entity.Size;
import com.clothingstore.backend.repository.ColorRepository;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.SizeRepository;
import com.clothingstore.backend.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getByProduct(@PathVariable String productId) {
        List<ProductVariantResponse> data = variantService.getByProductId(productId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Variants fetched", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductVariantResponse>> create(
            @PathVariable String productId,
            @RequestBody ProductVariantResponse request) {
        
        ProductVariant variant = toEntity(request, productId);
        ProductVariant created = variantService.create(variant);
        return ResponseEntity.ok(ApiResponse.success("Variant created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> update(
            @PathVariable String productId,
            @PathVariable String id,
            @RequestBody ProductVariantResponse request) {
        
        ProductVariant variant = toEntity(request, productId);
        variant.setId(id);
        ProductVariant updated = variantService.update(variant);
        return ResponseEntity.ok(ApiResponse.success("Variant updated", toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String productId,
            @PathVariable String id) {
        variantService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Variant deleted", null));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateStock(
            @PathVariable String productId,
            @PathVariable String id,
            @RequestParam Integer stockQuantity) {
        
        ProductVariant updated = variantService.updateStock(id, stockQuantity);
        return ResponseEntity.ok(ApiResponse.success("Stock updated", toResponse(updated)));
    }

    private ProductVariant toEntity(ProductVariantResponse request, String productId) {
        ProductVariant variant = new ProductVariant();
        
        if (productId != null) {
            Product product = new Product();
            product.setId(productId);
            variant.setProduct(product);
        }
        
        if (request.getColorId() != null) {
            Color color = new Color();
            color.setId(request.getColorId());
            variant.setColor(color);
        }
        
        if (request.getSizeId() != null) {
            Size size = new Size();
            size.setId(request.getSizeId());
            variant.setSize(size);
        }
        
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setStockQuantity(request.getStockQuantity());
        
        return variant;
    }

    private ProductVariantResponse toResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct() != null ? variant.getProduct().getId() : null)
                .colorId(variant.getColor() != null ? variant.getColor().getId() : null)
                .sizeId(variant.getSize() != null ? variant.getSize().getId() : null)
                .sku(variant.getSku())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}