package com.clothingstore.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ShippingZones")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShippingZone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shipping_zone_id")
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String region;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
