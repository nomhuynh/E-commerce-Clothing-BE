package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, String> {
    List<StockMovement> findByWarehouseId(String warehouseId);
    List<StockMovement> findByVariantId(String variantId);
    List<StockMovement> findByReferenceId(String referenceId);
}
