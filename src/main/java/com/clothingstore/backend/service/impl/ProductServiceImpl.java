package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product update(Product product) {
        if (product.getId() == null) {
            throw new RuntimeException("Product id is required for update");
        }
        if (!productRepository.existsById(product.getId())) {
            throw new RuntimeException("Product not found");
        }
        return productRepository.save(product);
    }

    @Override
    public Product getById(String id) {
        return productRepository.findByIdWithImages(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAllWithCategoryAndMaterial();
    }

    @Override
    public List<Product> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return productRepository.findAllWithCategoryAndMaterial();
        }
        return productRepository.searchByNameContainingIgnoreCase(keyword);
    }

    @Override
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
