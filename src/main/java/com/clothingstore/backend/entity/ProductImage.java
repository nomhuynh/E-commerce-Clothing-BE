package com.clothingstore.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ProductImages")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE ProductImages SET deleted_at = CURRENT_TIMESTAMP WHERE image_id = ?")
@SQLRestriction("deleted_at IS NULL")
@JsonIgnoreProperties({"product", "hibernateLazyInitializer", "handler"})
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "image_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "alt_text")
    private String altText;

    @Builder.Default
    @Column(name = "is_thumbnail")
    private Boolean isThumbnail = false;

    @Builder.Default
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
