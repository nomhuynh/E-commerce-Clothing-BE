package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.ProductVariant;
import com.clothingstore.backend.repository.ProductVariantRepository;
import com.clothingstore.backend.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Override
    public ProductVariant create(ProductVariant variant) {
        if (productVariantRepository.existsBySku(variant.getSku())) {
            throw new RuntimeException("SKU already exists");
        }
        return productVariantRepository.save(variant);
    }

    @Override
    public ProductVariant update(ProductVariant variant) {
        if (variant.getId() == null) {
            throw new RuntimeException("Variant id is required for update");
        }
        if (!productVariantRepository.existsById(variant.getId())) {
            throw new RuntimeException("Variant not found");
        }
        return productVariantRepository.save(variant);
    }

    @Override
    public ProductVariant getById(String id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }

    @Override
    public ProductVariant getBySku(String sku) {
        return productVariantRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }

    @Override
    public List<ProductVariant> getAll() {
        return productVariantRepository.findAll();
    }

    @Override
    public List<ProductVariant> getByProductId(String productId) {
        return productVariantRepository.findByProductId(productId);
    }

    @Override
    public ProductVariant updateStock(String id, Integer quantity, String operation) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Quantity must be >= 0");
        }

        int current = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
        int next;
        if ("set".equalsIgnoreCase(operation)) {
            next = quantity;
        } else if ("decrease".equalsIgnoreCase(operation)) {
            next = current - quantity;
        } else {
            next = current + quantity;
        }

        if (next < 0) {
            throw new RuntimeException("Insufficient stock");
        }

        variant.setStockQuantity(next);
        return productVariantRepository.save(variant);
    }

    @Override
    public void delete(String id) {
        if (!productVariantRepository.existsById(id)) {
            throw new RuntimeException("Variant not found");
        }
        productVariantRepository.deleteById(id);
    }
}
