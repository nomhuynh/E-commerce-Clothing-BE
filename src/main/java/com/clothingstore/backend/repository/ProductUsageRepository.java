package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.ProductUsage;
import com.clothingstore.backend.entity.ProductUsageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUsageRepository extends JpaRepository<ProductUsage, ProductUsageId> {
    List<ProductUsage> findByProductId(String productId);
    List<ProductUsage> findByUsageId(String usageId);
}
