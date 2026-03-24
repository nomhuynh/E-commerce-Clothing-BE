package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;

import java.util.List;

public interface ProductService {
    Product create(Product product);
    Product update(Product product);
    Product getById(String id);
    List<Product> getAll();
    void delete(String id);
    
    // Search & Filter
    List<Product> search(String keyword);
    List<Product> filter(
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
        String sortDir
    );
}