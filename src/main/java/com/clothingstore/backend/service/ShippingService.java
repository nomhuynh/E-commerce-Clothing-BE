package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.ShippingMethod;
import com.clothingstore.backend.entity.ShippingMethodZone;
import com.clothingstore.backend.entity.ShippingZone;

import java.math.BigDecimal;
import java.util.List;

public interface ShippingService {
    List<ShippingMethod> getMethods(Boolean activeOnly);
    ShippingMethod getMethodById(String methodId);

    ShippingMethod createShippingMethod(ShippingMethod method);
    ShippingMethod updateShippingMethod(String methodId, ShippingMethod method);
    void deleteShippingMethod(String methodId);

    List<ShippingZone> getZones(Boolean activeOnly);
    ShippingZone createShippingZone(ShippingZone zone);
    ShippingZone updateShippingZone(String zoneId, ShippingZone zone);
    void deleteShippingZone(String zoneId);

    List<ShippingMethodZone> getMethodZones(String methodId);
    ShippingMethodZone assignZoneToMethod(String methodId, String zoneId, BigDecimal fee, Integer estimatedDays);

    BigDecimal calculateShipping(String methodId, String zoneId);
}
