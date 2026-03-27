package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.ProductUsage;
import com.clothingstore.backend.entity.ProductUsageId;
import com.clothingstore.backend.repository.ProductUsageRepository;
import com.clothingstore.backend.service.ProductUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductUsageServiceImpl implements ProductUsageService {

    private final ProductUsageRepository productUsageRepository;

    @Override
    public ProductUsage create(ProductUsage usage) {
        return productUsageRepository.save(usage);
    }

    @Override
    public void delete(ProductUsageId id) {
        if (!productUsageRepository.existsById(id)) {
            throw new RuntimeException("Product usage not found");
        }
        productUsageRepository.deleteById(id);
    }

    @Override
    public List<ProductUsage> getByProductId(String productId) {
        return productUsageRepository.findByProductId(productId);
    }

    @Override
    public List<ProductUsage> getByUsageId(String usageId) {
        return productUsageRepository.findByUsageId(usageId);
    }
}
