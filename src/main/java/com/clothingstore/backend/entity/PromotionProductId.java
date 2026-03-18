package com.clothingstore.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionProductId implements Serializable {

    @Column(name = "promotion_id")
    private String promotionId;

    @Column(name = "product_id")
    private String productId;
}
