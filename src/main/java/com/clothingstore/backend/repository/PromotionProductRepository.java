package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.PromotionProduct;
import com.clothingstore.backend.entity.PromotionProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, PromotionProductId> {

    @Query("SELECT pp FROM PromotionProduct pp JOIN FETCH pp.product p WHERE pp.promotion.id = :promotionId ORDER BY p.name")
    List<PromotionProduct> findByPromotionIdWithProduct(@Param("promotionId") String promotionId);

    boolean existsByPromotion_IdAndProduct_Id(String promotionId, String productId);

    Optional<PromotionProduct> findByPromotion_IdAndProduct_Id(String promotionId, String productId);

    long countByPromotion_Id(String promotionId);

    @Query("SELECT pp.promotion.id, COUNT(pp) FROM PromotionProduct pp WHERE pp.promotion.id IN :ids GROUP BY pp.promotion.id")
    List<Object[]> countProductsGroupedByPromotionIds(@Param("ids") List<String> ids);

    @Query("""
            SELECT pp FROM PromotionProduct pp
            JOIN FETCH pp.promotion pr
            JOIN FETCH pp.product prod
            WHERE prod.id IN :productIds
            AND pr.isActive = true
            AND pr.startDate <= :now
            AND pr.endDate >= :now
            """)
    List<PromotionProduct> findActivePromotionLinksForProducts(
            @Param("productIds") List<String> productIds,
            @Param("now") LocalDateTime now);
}
