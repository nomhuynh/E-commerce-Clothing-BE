package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.ShippingMethod;
import com.clothingstore.backend.entity.ShippingMethodZone;
import com.clothingstore.backend.entity.ShippingZone;
import com.clothingstore.backend.repository.ShippingMethodRepository;
import com.clothingstore.backend.repository.ShippingMethodZoneRepository;
import com.clothingstore.backend.repository.ShippingZoneRepository;
import com.clothingstore.backend.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService {

    private final ShippingMethodRepository shippingMethodRepository;
    private final ShippingZoneRepository shippingZoneRepository;
    private final ShippingMethodZoneRepository shippingMethodZoneRepository;

    @Override
    public List<ShippingMethod> getMethods(Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return shippingMethodRepository.findByIsActiveTrue();
        }
        return shippingMethodRepository.findAll();
    }

    @Override
    public ShippingMethod getMethodById(String methodId) {
        return shippingMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));
    }

    @Override
    public ShippingMethod createShippingMethod(ShippingMethod method) {
        return shippingMethodRepository.save(method);
    }

    @Override
    public ShippingMethod updateShippingMethod(String methodId, ShippingMethod method) {
        ShippingMethod existing = getMethodById(methodId);
        if (method.getName() != null) existing.setName(method.getName());
        if (method.getDescription() != null) existing.setDescription(method.getDescription());
        if (method.getBaseFee() != null) existing.setBaseFee(method.getBaseFee());
        if (method.getEstimatedDays() != null) existing.setEstimatedDays(method.getEstimatedDays());
        if (method.getIsActive() != null) existing.setIsActive(method.getIsActive());
        return shippingMethodRepository.save(existing);
    }

    @Override
    public void deleteShippingMethod(String methodId) {
        ShippingMethod existing = getMethodById(methodId);
        shippingMethodRepository.delete(existing);
    }

    @Override
    public List<ShippingZone> getZones(Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return shippingZoneRepository.findByIsActiveTrue();
        }
        return shippingZoneRepository.findAll();
    }

    @Override
    public ShippingZone createShippingZone(ShippingZone zone) {
        return shippingZoneRepository.save(zone);
    }

    @Override
    public ShippingZone updateShippingZone(String zoneId, ShippingZone zone) {
        ShippingZone existing = shippingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Shipping zone not found"));
        if (zone.getName() != null) existing.setName(zone.getName());
        if (zone.getRegion() != null) existing.setRegion(zone.getRegion());
        if (zone.getIsActive() != null) existing.setIsActive(zone.getIsActive());
        return shippingZoneRepository.save(existing);
    }

    @Override
    public void deleteShippingZone(String zoneId) {
        ShippingZone existing = shippingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Shipping zone not found"));
        shippingZoneRepository.delete(existing);
    }

    @Override
    public List<ShippingMethodZone> getMethodZones(String methodId) {
        getMethodById(methodId);
        return shippingMethodZoneRepository.findByMethodId(methodId);
    }

    @Override
    public ShippingMethodZone assignZoneToMethod(String methodId, String zoneId, BigDecimal fee, Integer estimatedDays) {
        ShippingMethod method = getMethodById(methodId);
        ShippingZone zone = shippingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Shipping zone not found"));

        ShippingMethodZone mapping = shippingMethodZoneRepository.findByMethodIdAndZoneId(methodId, zoneId)
                .orElse(ShippingMethodZone.builder()
                        .method(method)
                        .zone(zone)
                        .fee(BigDecimal.ZERO)
                        .build());

        if (fee != null) mapping.setFee(fee);
        if (estimatedDays != null) mapping.setEstimatedDays(estimatedDays);
        return shippingMethodZoneRepository.save(mapping);
    }

    @Override
    public BigDecimal calculateShipping(String methodId, String zoneId) {
        ShippingMethodZone mapping = shippingMethodZoneRepository.findByMethodIdAndZoneId(methodId, zoneId)
                .orElse(null);
        if (mapping != null && mapping.getFee() != null) {
            return mapping.getFee();
        }

        ShippingMethod method = getMethodById(methodId);
        return method.getBaseFee() != null ? method.getBaseFee() : BigDecimal.ZERO;
    }
}
