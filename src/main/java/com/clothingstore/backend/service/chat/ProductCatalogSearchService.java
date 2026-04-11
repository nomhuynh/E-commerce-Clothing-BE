package com.clothingstore.backend.service.chat;

import com.clothingstore.backend.dto.chat.IntentExtractionResult;
import com.clothingstore.backend.dto.chat.ProductSnippet;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.enums.ProductGender;
import com.clothingstore.backend.entity.ProductImage;
import com.clothingstore.backend.entity.ProductVariant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tìm sản phẩm cho chat AI — dùng JPQL theo entity để Hibernate map đúng tên bảng MySQL
 * (tránh native SQL + phân biệt hoa/thường tên bảng như {@code productvariants}).
 */
@Slf4j
@Service
public class ProductCatalogSearchService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Tra cứu theo intent. Không dùng “fallback” sản phẩm mới nhất khi không khớp — tránh gợi ý túi/đồ lạ
     * khi khách hỏi quần jean mà kho không có.
     */
    public List<ProductSnippet> search(IntentExtractionResult intent, int limit) {
        return runQuery(intent, limit);
    }

    private List<ProductSnippet> runQuery(IntentExtractionResult intent, int limit) {
        List<String> rawKeywords = intent.keywords() == null ? List.of() : intent.keywords().stream()
                .filter(k -> k != null && !k.isBlank())
                .map(String::trim)
                .toList();
        List<String> keywords = expandFashionKeywords(rawKeywords);
        List<String> colors = intent.colors() == null ? List.of() : intent.colors().stream()
                .filter(c -> c != null && !c.isBlank())
                .map(String::trim)
                .toList();
        boolean hasKw = !keywords.isEmpty();
        boolean hasCol = !colors.isEmpty();
        boolean hasMax = intent.maxPrice() != null;

        if (!hasKw && !hasCol && !hasMax) {
            return List.of();
        }

        StringBuilder jpql = new StringBuilder("SELECT p.id FROM Product p WHERE p.deletedAt IS NULL");
        Map<String, Object> params = new HashMap<>();

        if (intent.genderFilter() != null) {
            if (intent.genderFilter() == ProductGender.UNISEX) {
                jpql.append(" AND p.gender = :genderFilter");
                params.put("genderFilter", ProductGender.UNISEX);
            } else {
                jpql.append(" AND (p.gender = :genderFilter OR p.gender = :unisexGender)");
                params.put("genderFilter", intent.genderFilter());
                params.put("unisexGender", ProductGender.UNISEX);
            }
        }

        if (intent.ageGroupFilter() != null) {
            jpql.append(" AND p.ageGroup = :ageGroupFilter");
            params.put("ageGroupFilter", intent.ageGroupFilter());
        }

        if (hasKw) {
            jpql.append(" AND (");
            for (int i = 0; i < keywords.size(); i++) {
                if (i > 0) {
                    jpql.append(" OR ");
                }
                String pk = "kw" + i;
                jpql.append("(LOWER(p.name) LIKE :").append(pk)
                        .append(" OR LOWER(COALESCE(p.description, '')) LIKE :").append(pk).append(")");
                params.put(pk, "%" + keywords.get(i).toLowerCase() + "%");
            }
            jpql.append(")");
        }

        if (hasCol) {
            jpql.append(" AND EXISTS (SELECT 1 FROM ProductVariant pv JOIN pv.color col ")
                    .append("WHERE pv.product = p AND pv.deletedAt IS NULL AND (");
            for (int i = 0; i < colors.size(); i++) {
                if (i > 0) {
                    jpql.append(" OR ");
                }
                String pc = "col" + i;
                jpql.append("LOWER(col.name) LIKE :").append(pc);
                params.put(pc, "%" + colors.get(i).toLowerCase() + "%");
            }
            jpql.append("))");
        }

        if (hasMax) {
            jpql.append("""
                     AND (COALESCE(
                       (SELECT MIN(v.price) FROM ProductVariant v WHERE v.product = p AND v.deletedAt IS NULL
                         AND (v.isActive IS NULL OR v.isActive = true)),
                       p.basePrice
                     ) <= :maxPrice)
                    """);
            params.put("maxPrice", intent.maxPrice());
        }

        jpql.append(" ORDER BY p.createdAt DESC");

        TypedQuery<String> idQuery = entityManager.createQuery(jpql.toString(), String.class);
        params.forEach(idQuery::setParameter);
        idQuery.setMaxResults(limit);
        List<String> ids = idQuery.getResultList();
        return loadSnippetsByIds(ids);
    }

    /**
     * Thêm biến thể từ khóa thời trang (VN/EN) để LIKE khớp tên SP trong kho tốt hơn.
     */
    static List<String> expandFashionKeywords(List<String> keywords) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String k : keywords) {
            if (k == null || k.isBlank()) {
                continue;
            }
            String t = k.trim();
            out.add(t);
            String low = t.toLowerCase();
            if (low.contains("jean")) {
                out.add("jean");
                out.add("jeans");
                out.add("quần jean");
                out.add("quan jean");
                out.add("denim");
            }
            if (low.contains("quần") || low.contains("quan")) {
                out.add("quần");
                out.add("quan");
            }
            if (low.contains("gym")
                    || low.contains("tập gym")
                    || low.contains("thể thao")
                    || low.contains("fitness")
                    || low.contains("yoga")) {
                out.add("gym");
                out.add("thể thao");
                out.add("tập");
                out.add("sport");
            }
            if (low.contains("sơ mi") || low.contains("so mi")) {
                out.add("sơ mi");
                out.add("somi");
            }
        }
        return new ArrayList<>(out);
    }

    private List<ProductSnippet> loadSnippetsByIds(List<String> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<Product> loaded = entityManager.createQuery(
                        "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants v LEFT JOIN FETCH v.color "
                                + "WHERE p.id IN :ids",
                        Product.class)
                .setParameter("ids", ids)
                .getResultList();
        Map<String, Product> byId = loaded.stream().collect(Collectors.toMap(Product::getId, x -> x, (a, b) -> a));
        List<ProductSnippet> out = new ArrayList<>();
        for (String id : ids) {
            Product p = byId.get(id);
            if (p != null) {
                out.add(toSnippet(p));
            }
        }
        return out;
    }

    private static ProductSnippet toSnippet(Product p) {
        BigDecimal price = p.getBasePrice();
        String colorName = null;
        List<ProductVariant> variants = p.getVariants();
        if (variants != null && !variants.isEmpty()) {
            List<ProductVariant> active = variants.stream()
                    .filter(v -> v.getDeletedAt() == null)
                    .filter(v -> v.getIsActive() == null || Boolean.TRUE.equals(v.getIsActive()))
                    .filter(v -> v.getPrice() != null)
                    .sorted(Comparator.comparing(ProductVariant::getPrice))
                    .toList();
            if (!active.isEmpty()) {
                ProductVariant cheapest = active.get(0);
                price = cheapest.getPrice();
                if (cheapest.getColor() != null) {
                    colorName = cheapest.getColor().getName();
                }
            }
        }
        return new ProductSnippet(
                Objects.requireNonNullElse(p.getId(), ""),
                Objects.requireNonNullElse(p.getName(), ""),
                colorName,
                price != null ? price : BigDecimal.ZERO,
                pickImageUrl(p));
    }

    private static String pickImageUrl(Product p) {
        List<ProductImage> imgs = p.getImages();
        if (imgs == null || imgs.isEmpty()) {
            return null;
        }
        return imgs.stream()
                .filter(Objects::nonNull)
                .min(Comparator
                        .comparing((ProductImage i) -> !Boolean.TRUE.equals(i.getIsThumbnail()))
                        .thenComparing(i -> Optional.ofNullable(i.getSortOrder()).orElse(0))
                        .thenComparing(ProductImage::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }
}
