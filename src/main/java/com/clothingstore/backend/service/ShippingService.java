package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.ShippingMethod;

import java.util.List;

public interface ShippingService {
    List<ShippingMethod> getActiveMethods();
}
