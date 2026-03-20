package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.entity.ShippingMethod;
import com.clothingstore.backend.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @GetMapping("/methods")
    public ResponseEntity<ApiResponse<List<ShippingMethod>>> getMethods() {
        return ResponseEntity.ok(ApiResponse.success("Shipping methods fetched", shippingService.getActiveMethods()));
    }
}
