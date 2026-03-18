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
public class ProductUsageId implements Serializable {

    @Column(name = "product_id")
    private String productId;

    @Column(name = "usage_id")
    private String usageId;
}
