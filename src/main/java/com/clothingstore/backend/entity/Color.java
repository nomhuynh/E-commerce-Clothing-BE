package com.clothingstore.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Colors")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE Colors SET deleted_at = CURRENT_TIMESTAMP WHERE color_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Color extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "color_id")
    private String id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "hex_code", length = 7)
    private String hexCode;
}
