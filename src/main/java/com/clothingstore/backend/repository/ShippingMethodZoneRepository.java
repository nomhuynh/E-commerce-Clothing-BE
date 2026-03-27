package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.ShippingMethodZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingMethodZoneRepository extends JpaRepository<ShippingMethodZone, String> {
    Optional<ShippingMethodZone> findByMethodIdAndZoneId(String methodId, String zoneId);
    List<ShippingMethodZone> findByMethodId(String methodId);
    List<ShippingMethodZone> findByZoneId(String zoneId);
}
