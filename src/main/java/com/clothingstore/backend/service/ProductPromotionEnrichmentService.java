package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.product.ProductActivePromotionDto;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.ProductVariant;
import com.clothingstore.backend.entity.Promotion;
import com.clothingstore.backend.entity.PromotionProduct;
import com.clothingstore.backend.entity.enums.DiscountType;
import com.clothingstore.backend.repository.PromotionProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPromotionEnrichmentService {

    private final PromotionProductRepository promotionProductRepository;

    /**
     * Attaches {@link Product#setActivePromotion} when an active promotion yields a price strictly below the list price.
     */
    public void enrichProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<String> ids = products.stream().map(Product::getId).filter(Objects::nonNull).toList();
        if (ids.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<PromotionProduct> links = promotionProductRepository.findActivePromotionLinksForProducts(ids, now);
        Map<String, List<Promotion>> byProductId = links.stream()
                .collect(Collectors.groupingBy(
                        pp -> pp.getProduct().getId(),
                        Collectors.mapping(PromotionProduct::getPromotion, Collectors.toList())));

        for (Product p : products) {
            BigDecimal list = effectiveListPrice(p);
            if (list == null || list.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            List<Promotion> promos = byProductId.getOrDefault(p.getId(), List.of());
            Promotion best = selectBestPromotion(list, promos);
            if (best == null) {
                continue;
            }
            BigDecimal finalPrice = applyDiscount(list, best);
            if (finalPrice.compareTo(list) < 0) {
                p.setActivePromotion(ProductActivePromotionDto.builder()
                        .discountType(best.getDiscountType().name())
                        .discountValue(best.getDiscountValue())
                        .build());
            }
        }
    }

    /** Same rule as storefront listing: first variant price, else base price. */
    private static BigDecimal effectiveListPrice(Product p) {
        List<ProductVariant> vs = p.getVariants();
        if (vs != null && !vs.isEmpty()) {
            ProductVariant first = vs.get(0);
            if (first.getPrice() != null && first.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                return first.getPrice();
            }
        }
        return p.getBasePrice() != null ? p.getBasePrice() : BigDecimal.ZERO;
    }

    private static Promotion selectBestPromotion(BigDecimal listPrice, List<Promotion> promotions) {
        if (promotions == null || promotions.isEmpty()) {
            return null;
        }
        Promotion best = null;
        BigDecimal bestFinal = null;
        for (Promotion pr : promotions) {
            BigDecimal fp = applyDiscount(listPrice, pr);
            if (fp.compareTo(listPrice) >= 0) {
                continue;
            }
            if (best == null || fp.compareTo(bestFinal) < 0) {
                best = pr;
                bestFinal = fp;
            }
        }
        return best;
    }

    private static BigDecimal applyDiscount(BigDecimal listPrice, Promotion prom) {
        if (listPrice == null || listPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return listPrice;
        }
        DiscountType t = prom.getDiscountType();
        if (t == DiscountType.percentage) {
            BigDecimal pct = prom.getDiscountValue();
            BigDecimal factor = BigDecimal.ONE.subtract(
                    pct.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
            return listPrice.multiply(factor).setScale(0, RoundingMode.HALF_UP);
        }
        return listPrice.subtract(prom.getDiscountValue()).max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP);
    }
}

