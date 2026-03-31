package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.ShippingMethodZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingMethodZoneRepository extends JpaRepository<ShippingMethodZone, String> {
    List<ShippingMethodZone> findByMethodId(String shippingMethodId);
    List<ShippingMethodZone> findByZoneId(String shippingZoneId);
    Optional<ShippingMethodZone> findByMethodIdAndZoneId(String shippingMethodId, String shippingZoneId);
}
