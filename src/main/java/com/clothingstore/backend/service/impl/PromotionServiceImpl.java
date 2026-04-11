package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.promotion.PromotionProductRowDto;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.Promotion;
import com.clothingstore.backend.entity.PromotionProduct;
import com.clothingstore.backend.entity.PromotionProductId;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.PromotionProductRepository;
import com.clothingstore.backend.repository.PromotionRepository;
import com.clothingstore.backend.service.PromotionService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductRepository productRepository;

    @Override
    public Promotion create(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion update(Promotion promotion) {
        if (promotion.getId() == null) {
            throw new RuntimeException("Promotion id is required for update");
        }
        if (!promotionRepository.existsById(promotion.getId())) {
            throw new RuntimeException("Promotion not found");
        }
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion getById(String id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    @Override
    public List<Promotion> getAll() {
        return promotionRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Promotion not found");
        }
        promotionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Promotion> findPageForAdmin(int page, int limit, String q, Boolean isActive) {
        int p = Math.max(1, page);
        int l = Math.min(100, Math.max(1, limit));
        Specification<Promotion> spec = buildAdminSpec(q, isActive);
        return promotionRepository.findAll(
                spec,
                PageRequest.of(p - 1, l, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    private static Specification<Promotion> buildAdminSpec(String q, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                preds.add(cb.like(cb.lower(root.get("name")), pattern));
            }
            if (isActive != null) {
                preds.add(cb.equal(root.get("isActive"), isActive));
            }
            return cb.and(preds.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countProductsByPromotionIds(List<String> promotionIds) {
        Map<String, Long> map = new HashMap<>();
        if (promotionIds == null || promotionIds.isEmpty()) {
            return map;
        }
        for (Object[] row : promotionProductRepository.countProductsGroupedByPromotionIds(promotionIds)) {
            if (row[0] != null && row[1] != null) {
                map.put((String) row[0], ((Number) row[1]).longValue());
            }
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionProductRowDto> listProductsForPromotion(String promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new RuntimeException("Promotion not found");
        }
        return promotionProductRepository.findByPromotionIdWithProduct(promotionId).stream()
                .map(pp -> {
                    Product pr = pp.getProduct();
                    return PromotionProductRowDto.builder()
                            .productId(pr.getId())
                            .name(pr.getName())
                            .basePrice(pr.getBasePrice())
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional
    public void addProductsToPromotion(String promotionId, List<String> productIds) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        for (String pid : productIds) {
            if (pid == null || pid.isBlank()) {
                continue;
            }
            if (promotionProductRepository.existsByPromotion_IdAndProduct_Id(promotionId, pid)) {
                continue;
            }
            Product product = productRepository.findById(pid)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + pid));
            PromotionProductId compositeId = new PromotionProductId(promotionId, pid);
            PromotionProduct link = new PromotionProduct();
            link.setId(compositeId);
            link.setPromotion(promotion);
            link.setProduct(product);
            promotionProductRepository.save(link);
        }
    }

    @Override
    @Transactional
    public void removeProductFromPromotion(String promotionId, String productId) {
        promotionProductRepository.findByPromotion_IdAndProduct_Id(promotionId, productId)
                .ifPresentOrElse(
                        promotionProductRepository::delete,
                        () -> {
                            throw new RuntimeException("Product not linked to this promotion");
                        });
    }
}
