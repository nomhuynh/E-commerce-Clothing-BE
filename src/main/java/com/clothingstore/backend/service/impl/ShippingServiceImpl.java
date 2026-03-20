package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.ShippingMethod;
import com.clothingstore.backend.repository.ShippingMethodRepository;
import com.clothingstore.backend.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService {

    private final ShippingMethodRepository shippingMethodRepository;

    @Override
    public List<ShippingMethod> getActiveMethods() {
        return shippingMethodRepository.findByIsActiveTrue();
    }
}
