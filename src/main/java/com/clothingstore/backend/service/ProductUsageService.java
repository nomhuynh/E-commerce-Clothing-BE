package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.ProductUsage;
import com.clothingstore.backend.entity.ProductUsageId;

import java.util.List;

public interface ProductUsageService {
    ProductUsage create(ProductUsage usage);
    void delete(ProductUsageId id);
    List<ProductUsage> getByProductId(String productId);
    List<ProductUsage> getByUsageId(String usageId);
}
