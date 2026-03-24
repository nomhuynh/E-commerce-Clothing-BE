package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.ProductImage;
import com.clothingstore.backend.repository.ProductImageRepository;
import com.clothingstore.backend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public ProductImage create(ProductImage image) {
        return productImageRepository.save(image);
    }

    @Override
    @Transactional
    public ProductImage update(ProductImage image) {
        ProductImage existing = productImageRepository.findById(image.getId())
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        if (image.getImageUrl() != null) existing.setImageUrl(image.getImageUrl());
        if (image.getDisplayOrder() != null) existing.setDisplayOrder(image.getDisplayOrder());
        
        return productImageRepository.save(existing);
    }

    @Override
    public ProductImage getById(String id) {
        return productImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    @Override
    public List<ProductImage> getByProductId(String productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!productImageRepository.existsById(id)) {
            throw new RuntimeException("Image not found");
        }
        productImageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductImage setThumbnail(String id) {
        ProductImage image = getById(id);
        String productId = image.getProduct().getId();
        
        // Reset all thumbnails for this product
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        for (ProductImage img : images) {
            img.setIsThumbnail(false);
            productImageRepository.save(img);
        }
        
        // Set new thumbnail
        image.setIsThumbnail(true);
        return productImageRepository.save(image);
    }

    @Override
    @Transactional
    public void reorder(List<String> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            ProductImage image = getById(imageIds.get(i));
            image.setDisplayOrder(i);
            productImageRepository.save(image);
        }
    }
}