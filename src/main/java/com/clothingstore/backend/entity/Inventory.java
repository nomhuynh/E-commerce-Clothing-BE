package com.clothingstore.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Inventories", uniqueConstraints = {
        @UniqueConstraint(name = "unique_inventory_per_warehouse_variant", columnNames = {"warehouse_id", "variant_id"})
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE Inventories SET deleted_at = CURRENT_TIMESTAMP WHERE inventory_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inventory_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Builder.Default
    @Column(name = "on_hand", nullable = false)
    private Integer onHand = 0;

    @Builder.Default
    @Column(name = "reserved", nullable = false)
    private Integer reserved = 0;
}
