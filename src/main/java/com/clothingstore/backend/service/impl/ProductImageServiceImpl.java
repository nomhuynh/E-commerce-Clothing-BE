package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.ProductImage;
import com.clothingstore.backend.repository.ProductImageRepository;
import com.clothingstore.backend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Override
    public ProductImage create(ProductImage image) {
        return productImageRepository.save(image);
    }

    @Override
    public ProductImage update(ProductImage image) {
        if (image.getId() == null) {
            throw new RuntimeException("Image id is required for update");
        }
        if (!productImageRepository.existsById(image.getId())) {
            throw new RuntimeException("Image not found");
        }
        return productImageRepository.save(image);
    }

    @Override
    public ProductImage getById(String id) {
        return productImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    @Override
    public List<ProductImage> getByProductId(String productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    public List<ProductImage> getAll() {
        return productImageRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!productImageRepository.existsById(id)) {
            throw new RuntimeException("Image not found");
        }
        productImageRepository.deleteById(id);
    }
}
