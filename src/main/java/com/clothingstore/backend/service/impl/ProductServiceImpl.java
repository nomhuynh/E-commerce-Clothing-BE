package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.service.ProductPromotionEnrichmentService;
import com.clothingstore.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    
    private final ProductRepository productRepository;
    private final ProductPromotionEnrichmentService productPromotionEnrichmentService;

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
    @Transactional(readOnly = true)
    public Product getById(String id) {
        Product p = productRepository.findByIdWithImages(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        p.getVariants().size();
        productPromotionEnrichmentService.enrichProducts(Collections.singletonList(p));
        return p;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAll() {
        List<Product> list = productRepository.findAllWithCategoryAndMaterial();
        warmVariantCollections(list);
        productPromotionEnrichmentService.enrichProducts(list);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> search(String keyword) {
        List<Product> list = keyword == null || keyword.isBlank()
                ? productRepository.findAllWithCategoryAndMaterial()
                : productRepository.searchByNameContainingIgnoreCase(keyword);
        warmVariantCollections(list);
        productPromotionEnrichmentService.enrichProducts(list);
        return list;
    }

    /** Ensures variants are loaded before JSON serialization (cart needs variant id on listing). */
    private static void warmVariantCollections(List<Product> products) {
        for (Product p : products) {
            p.getVariants().size();
        }
    }

    @Override
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
