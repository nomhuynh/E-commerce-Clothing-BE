package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.ProductVariant;

import java.util.List;

public interface ProductVariantService {
    ProductVariant create(ProductVariant variant);
    ProductVariant update(ProductVariant variant);
    ProductVariant getById(String id);
    ProductVariant getBySku(String sku);
    List<ProductVariant> getAll();
    List<ProductVariant> getByProductId(String productId);
    ProductVariant updateStock(String id, Integer quantity, String operation);
    void delete(String id);
}
