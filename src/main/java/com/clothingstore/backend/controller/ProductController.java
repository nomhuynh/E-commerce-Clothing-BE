package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.ProductVariant;
import com.clothingstore.backend.service.ProductService;
import com.clothingstore.backend.service.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;

    @Operation(summary = "Get products")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts() {
        return ResponseEntity.ok(ApiResponse.success("Products fetched", productService.getAll()));
    }

    @Operation(summary = "Search products")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.success("Products fetched", productService.search(q)));
    }

    @Operation(summary = "Get filter options")
    @GetMapping("/filters")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFilterOptions() {
        return ResponseEntity.ok(ApiResponse.success("Filter options fetched", Map.of()));
    }

    @Operation(summary = "Get product by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", productService.getById(id)));
    }

    @Operation(summary = "Create product")
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.create(product)));
    }

    @Operation(summary = "Update product")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable String id, @RequestBody Product product) {
        product.setId(id);
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.update(product)));
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    @Operation(summary = "Get product variants")
    @GetMapping("/{productId}/variants")
    public ResponseEntity<ApiResponse<List<ProductVariant>>> getVariantsByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success("Variants fetched", productVariantService.getByProductId(productId)));
    }

    @Operation(summary = "Create product variant")
    @PostMapping("/{productId}/variants")
    public ResponseEntity<ApiResponse<ProductVariant>> createVariant(@PathVariable String productId, @RequestBody ProductVariant variant) {
        if (variant.getProduct() == null) {
            Product p = new Product();
            p.setId(productId);
            variant.setProduct(p);
        }
        return ResponseEntity.ok(ApiResponse.success("Variant created", productVariantService.create(variant)));
    }

    @Operation(summary = "Update product variant")
    @PatchMapping("/{productId}/variants/{id}")
    public ResponseEntity<ApiResponse<ProductVariant>> updateVariant(@PathVariable String productId,
                                                                     @PathVariable String id,
                                                                     @RequestBody ProductVariant variant) {
        variant.setId(id);
        if (variant.getProduct() == null) {
            Product p = new Product();
            p.setId(productId);
            variant.setProduct(p);
        }
        return ResponseEntity.ok(ApiResponse.success("Variant updated", productVariantService.update(variant)));
    }

    @Operation(summary = "Delete product variant")
    @DeleteMapping("/{productId}/variants/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable String productId,
                                                           @PathVariable String id) {
        productVariantService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Variant deleted", null));
    }

    @Operation(summary = "Update variant stock")
    @PatchMapping("/{productId}/variants/{id}/stock")
    public ResponseEntity<ApiResponse<ProductVariant>> updateStock(@PathVariable String productId,
                                                                   @PathVariable String id,
                                                                   @RequestParam Integer quantity,
                                                                   @RequestParam(defaultValue = "increase") String operation) {
        return ResponseEntity.ok(ApiResponse.success("Stock updated", productVariantService.updateStock(id, quantity, operation)));
    }
}
