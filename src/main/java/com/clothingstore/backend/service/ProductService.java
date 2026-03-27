package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(Product product);
    Product update(Product product);
    Product getById(String id);
    List<Product> getAll();
    void delete(String id);
}
