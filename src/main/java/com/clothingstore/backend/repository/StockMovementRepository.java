package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, String> {
    List<StockMovement> findByWarehouseIdOrderByCreatedAtDesc(String warehouseId);
    List<StockMovement> findByVariantIdOrderByCreatedAtDesc(String variantId);
    List<StockMovement> findByReferenceIdOrderByCreatedAtDesc(String referenceId);
}
