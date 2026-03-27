package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, String> {
    List<ShippingMethod> findByIsActiveTrue();
}
