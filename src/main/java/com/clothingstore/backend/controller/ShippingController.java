package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.shipping.AssignShippingZoneRequest;
import com.clothingstore.backend.dto.shipping.ShippingCalculateRequest;
import com.clothingstore.backend.entity.ShippingMethod;
import com.clothingstore.backend.entity.ShippingMethodZone;
import com.clothingstore.backend.entity.ShippingZone;
import com.clothingstore.backend.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
@Tag(name = "Shipping")
public class ShippingController {

    private final ShippingService shippingService;

    @Operation(summary = "Get shipping methods")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShippingMethod>>> getShippingMethods(
            @RequestParam(defaultValue = "true") boolean active) {
        return ResponseEntity.ok(ApiResponse.success("Shipping methods fetched", shippingService.getMethods(active)));
    }

    @Operation(summary = "Get shipping method by id")
    @GetMapping("/{methodId}")
    public ResponseEntity<ApiResponse<ShippingMethod>> getShippingMethodById(@PathVariable String methodId) {
        return ResponseEntity.ok(ApiResponse.success("Shipping method fetched", shippingService.getMethodById(methodId)));
    }

    @Operation(summary = "Calculate shipping")
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateShipping(@Valid @RequestBody ShippingCalculateRequest request) {
        BigDecimal fee = shippingService.calculateShipping(request.getMethodId(), request.getZoneId());
        return ResponseEntity.ok(ApiResponse.success("Shipping calculated", Map.of("fee", fee)));
    }

    @Operation(summary = "Create shipping method")
    @PostMapping
    public ResponseEntity<ApiResponse<ShippingMethod>> createShippingMethod(@RequestBody ShippingMethod method) {
        return ResponseEntity.ok(ApiResponse.success("Shipping method created", shippingService.createShippingMethod(method)));
    }

    @Operation(summary = "Update shipping method")
    @PatchMapping("/{methodId}")
    public ResponseEntity<ApiResponse<ShippingMethod>> updateShippingMethod(
            @PathVariable String methodId,
            @RequestBody ShippingMethod method) {
        return ResponseEntity.ok(ApiResponse.success("Shipping method updated", shippingService.updateShippingMethod(methodId, method)));
    }

    @Operation(summary = "Delete shipping method")
    @DeleteMapping("/{methodId}")
    public ResponseEntity<ApiResponse<Void>> deleteShippingMethod(@PathVariable String methodId) {
        shippingService.deleteShippingMethod(methodId);
        return ResponseEntity.ok(ApiResponse.success("Shipping method deleted", null));
    }

    @Operation(summary = "Get shipping zones")
    @GetMapping("/zones")
    public ResponseEntity<ApiResponse<List<ShippingZone>>> getShippingZones(
            @RequestParam(defaultValue = "true") boolean active) {
        return ResponseEntity.ok(ApiResponse.success("Shipping zones fetched", shippingService.getZones(active)));
    }

    @Operation(summary = "Create shipping zone")
    @PostMapping("/zones")
    public ResponseEntity<ApiResponse<ShippingZone>> createShippingZone(@RequestBody ShippingZone zone) {
        return ResponseEntity.ok(ApiResponse.success("Shipping zone created", shippingService.createShippingZone(zone)));
    }

    @Operation(summary = "Update shipping zone")
    @PatchMapping("/zones/{zoneId}")
    public ResponseEntity<ApiResponse<ShippingZone>> updateShippingZone(
            @PathVariable String zoneId,
            @RequestBody ShippingZone zone) {
        return ResponseEntity.ok(ApiResponse.success("Shipping zone updated", shippingService.updateShippingZone(zoneId, zone)));
    }

    @Operation(summary = "Delete shipping zone")
    @DeleteMapping("/zones/{zoneId}")
    public ResponseEntity<ApiResponse<Void>> deleteShippingZone(@PathVariable String zoneId) {
        shippingService.deleteShippingZone(zoneId);
        return ResponseEntity.ok(ApiResponse.success("Shipping zone deleted", null));
    }

    @Operation(summary = "Get method zones")
    @GetMapping("/{methodId}/zones")
    public ResponseEntity<ApiResponse<List<ShippingMethodZone>>> getMethodZones(@PathVariable String methodId) {
        return ResponseEntity.ok(ApiResponse.success("Method zones fetched", shippingService.getMethodZones(methodId)));
    }

    @Operation(summary = "Assign zone to method")
    @PostMapping("/{methodId}/zones")
    public ResponseEntity<ApiResponse<ShippingMethodZone>> assignZoneToMethod(
            @PathVariable String methodId,
            @Valid @RequestBody AssignShippingZoneRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Zone assigned",
                shippingService.assignZoneToMethod(methodId, request.getZoneId(), request.getFee(), request.getEstimatedDays())
        ));
    }
}
