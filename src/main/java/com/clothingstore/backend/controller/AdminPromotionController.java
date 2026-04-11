package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.admin.AdminPromotionListPayload;
import com.clothingstore.backend.dto.promotion.AddPromotionProductsRequest;
import com.clothingstore.backend.dto.promotion.AdminPromotionResponse;
import com.clothingstore.backend.dto.promotion.PromotionProductRowDto;
import com.clothingstore.backend.dto.promotion.PromotionRequest;
import com.clothingstore.backend.entity.Promotion;
import com.clothingstore.backend.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminPromotionResponse>> create(@Valid @RequestBody PromotionRequest request) {
        Promotion created = promotionService.create(toEntity(request));
        return ResponseEntity.ok(ApiResponse.success("Promotion created", toAdminResponse(created, 0L, null)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminPromotionResponse>> update(
            @PathVariable String id, @Valid @RequestBody PromotionRequest request) {
        Promotion entity = toEntity(request);
        entity.setId(id);
        Promotion updated = promotionService.update(entity);
        long cnt = promotionService.countProductsByPromotionIds(List.of(id)).getOrDefault(id, 0L);
        return ResponseEntity.ok(ApiResponse.success("Promotion updated", toAdminResponse(updated, cnt, null)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminPromotionResponse>> patch(
            @PathVariable String id, @Valid @RequestBody PromotionRequest request) {
        return update(id, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminPromotionResponse>> getById(@PathVariable String id) {
        Promotion p = promotionService.getById(id);
        List<PromotionProductRowDto> products = promotionService.listProductsForPromotion(id);
        long cnt = products.size();
        return ResponseEntity.ok(ApiResponse.success(
                "Promotion fetched",
                toAdminResponse(p, cnt, products)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AdminPromotionListPayload>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean is_active) {
        Page<Promotion> pg = promotionService.findPageForAdmin(page, limit, q, is_active);
        List<String> ids = pg.getContent().stream().map(Promotion::getId).toList();
        Map<String, Long> counts = promotionService.countProductsByPromotionIds(ids);
        List<AdminPromotionResponse> rows = pg.getContent().stream()
                .map(promo -> toAdminResponse(
                        promo,
                        counts.getOrDefault(promo.getId(), 0L),
                        null))
                .toList();
        AdminPromotionListPayload payload = AdminPromotionListPayload.builder()
                .promotions(rows)
                .total(pg.getTotalElements())
                .page(page)
                .totalPages(Math.max(1, pg.getTotalPages()))
                .limit(limit)
                .build();
        return ResponseEntity.ok(ApiResponse.success("Promotions fetched", payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        promotionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Promotion deleted", null));
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<ApiResponse<Void>> addProducts(
            @PathVariable String id,
            @RequestBody AddPromotionProductsRequest body) {
        List<String> ids = body.getProductIds() != null
                ? body.getProductIds().stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.toList())
                : List.of();
        promotionService.addProductsToPromotion(id, ids);
        return ResponseEntity.ok(ApiResponse.success("Products added to promotion", null));
    }

    @DeleteMapping("/{promotionId}/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeProduct(
            @PathVariable String promotionId,
            @PathVariable String productId) {
        promotionService.removeProductFromPromotion(promotionId, productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from promotion", null));
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

    private AdminPromotionResponse toAdminResponse(
            Promotion p, long productCount, List<PromotionProductRowDto> products) {
        AdminPromotionResponse.AdminPromotionResponseBuilder b = AdminPromotionResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .discountType(p.getDiscountType())
                .discountValue(p.getDiscountValue())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .productCount((int) productCount);
        if (products != null) {
            b.products(products);
        }
        return b.build();
    }
}
