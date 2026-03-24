package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.promotion.PromotionRequest;
import com.clothingstore.backend.dto.promotion.PromotionResponse;
import com.clothingstore.backend.entity.Promotion;
import com.clothingstore.backend.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PromotionResponse>> create(@Valid @RequestBody PromotionRequest request) {
        Promotion created = promotionService.create(toEntity(request));
        return ResponseEntity.ok(ApiResponse.success("Promotion created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionResponse>> update(@PathVariable String id, @Valid @RequestBody PromotionRequest request) {
        Promotion entity = toEntity(request);
        entity.setId(id);
        Promotion updated = promotionService.update(entity);
        return ResponseEntity.ok(ApiResponse.success("Promotion updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Promotion fetched", toResponse(promotionService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getAll() {
        List<PromotionResponse> data = promotionService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Promotions fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        promotionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Promotion deleted", null));
    }

    private Promotion toEntity(PromotionRequest request) {
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return promotion;
    }

    private PromotionResponse toResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.getIsActive())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }
}