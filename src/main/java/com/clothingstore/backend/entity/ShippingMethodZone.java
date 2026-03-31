package com.clothingstore.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "ShippingMethodZones", uniqueConstraints = {
        @UniqueConstraint(name = "uk_shipping_method_zone", columnNames = {"shipping_method_id", "shipping_zone_id"})
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShippingMethodZone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id", nullable = false)
    private ShippingMethod method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_zone_id", nullable = false)
    private ShippingZone zone;

    @Builder.Default
    @Column(name = "fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(name = "estimated_days")
    private Integer estimatedDays;
}
