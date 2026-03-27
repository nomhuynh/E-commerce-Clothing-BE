package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Optional<Inventory> findByWarehouseIdAndVariantId(String warehouseId, String variantId);
    List<Inventory> findByWarehouseId(String warehouseId);
    List<Inventory> findByVariantId(String variantId);
}
