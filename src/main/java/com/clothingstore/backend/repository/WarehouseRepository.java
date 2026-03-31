package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    List<Warehouse> findByIsActiveTrue();
}
