package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingZoneRepository extends JpaRepository<ShippingZone, String> {
    List<ShippingZone> findByIsActiveTrue();
}
