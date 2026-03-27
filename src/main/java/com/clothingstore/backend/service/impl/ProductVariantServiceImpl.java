package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.ProductVariant;
import com.clothingstore.backend.repository.ProductVariantRepository;
import com.clothingstore.backend.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public ProductVariant create(ProductVariant variant) {
        if (productVariantRepository.existsBySku(variant.getSku())) {
            throw new RuntimeException("SKU already exists");
        }
        return productVariantRepository.save(variant);
    }

    @Override
    @Transactional
    public ProductVariant update(ProductVariant variant) {
        ProductVariant existing = productVariantRepository.findById(variant.getId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (variant.getSku() != null) existing.setSku(variant.getSku());
        if (variant.getPrice() != null) existing.setPrice(variant.getPrice());

        return productVariantRepository.save(existing);
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
    @Transactional
    public void delete(String id) {
        if (!productVariantRepository.existsById(id)) {
            throw new RuntimeException("Variant not found");
        }
        productVariantRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductVariant updateStock(String id, Integer stockQuantity) {
        ProductVariant variant = getById(id);
        variant.setStockQuantity(stockQuantity);
        return productVariantRepository.save(variant);
    }
}
