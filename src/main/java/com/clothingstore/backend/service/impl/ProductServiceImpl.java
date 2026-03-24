package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product update(Product product) {
        Product existing = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getName() != null) existing.setName(product.getName());
        if (product.getDescription() != null) existing.setDescription(product.getDescription());
        if (product.getBasePrice() != null) existing.setBasePrice(product.getBasePrice());
        if (product.getGender() != null) existing.setGender(product.getGender());
        if (product.getAgeGroup() != null) existing.setAgeGroup(product.getAgeGroup());
        if (product.getCategory() != null) existing.setCategory(product.getCategory());
        if (product.getMaterial() != null) existing.setMaterial(product.getMaterial());
        
        return productRepository.save(existing);
    }

    @Override
    public Product getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> search(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    @Override
    public List<Product> filter(
            String categoryId,
            String materialId,
            ProductGender gender,
            AgeGroup ageGroup,
            Double minPrice,
            Double maxPrice,
            String colorId,
            String sizeId,
            Boolean inStock,
            Integer page,
            Integer limit,
            String sortBy,
            String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "DESC"), 
                           sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, 
                                           limit != null ? limit : 10, 
                                           sort);
        
        Page<Product> result = productRepository.filter(
            categoryId, materialId, gender, ageGroup, minPrice, maxPrice, pageable
        );
        
        return result.getContent();
    }
}