package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.promotion.PromotionProductRowDto;
import com.clothingstore.backend.entity.Promotion;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PromotionService {
    Promotion create(Promotion promotion);
    Promotion update(Promotion promotion);
    Promotion getById(String id);
    List<Promotion> getAll();
    void delete(String id);

    Page<Promotion> findPageForAdmin(int page, int limit, String q, Boolean isActive);

    Map<String, Long> countProductsByPromotionIds(List<String> promotionIds);

    List<PromotionProductRowDto> listProductsForPromotion(String promotionId);

    void addProductsToPromotion(String promotionId, List<String> productIds);

    void removeProductFromPromotion(String promotionId, String productId);
}
