package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.ProductImage;

import java.util.List;

public interface ProductImageService {
    ProductImage create(ProductImage image);
    ProductImage update(ProductImage image);
    ProductImage getById(String id);
    List<ProductImage> getByProductId(String productId);
    List<ProductImage> getAll();
    void delete(String id);
}
