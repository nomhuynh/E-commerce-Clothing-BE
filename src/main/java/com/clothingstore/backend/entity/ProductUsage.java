package com.clothingstore.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ProductUsages")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE ProductUsages SET deleted_at = CURRENT_TIMESTAMP WHERE product_id = ? AND usage_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ProductUsage extends BaseEntity {

    @EmbeddedId
    private ProductUsageId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @MapsId("usageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usage_id", nullable = false)
    private Usage usage;
}
