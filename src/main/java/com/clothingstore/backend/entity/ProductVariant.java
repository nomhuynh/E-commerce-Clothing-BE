package com.clothingstore.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductVariants", uniqueConstraints = {
        @UniqueConstraint(name = "unique_product_variant_combination", columnNames = {"product_id", "color_id", "size_id"})
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE ProductVariants SET deleted_at = CURRENT_TIMESTAMP WHERE variant_id = ?")
@SQLRestriction("deleted_at IS NULL")
@JsonIgnoreProperties({"product", "hibernateLazyInitializer", "handler"})
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "variant_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Column(name = "sku", nullable = false, length = 50, unique = true)
    private String sku;

    @Column(name = "image")
    private String image;

    @Column(name = "original_price", precision = 15, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "weight")
    private Float weight;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
