package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    List<ProductImage> findByProductId(String productId);
    List<ProductImage> findByProductIdOrderBySortOrderAsc(String productId);
}
