package com.clothingstore.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "ShippingMethods")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShippingMethod extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shipping_method_id")
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_fee", precision = 10, scale = 2)
    private BigDecimal baseFee;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
