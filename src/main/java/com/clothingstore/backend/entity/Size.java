package com.clothingstore.backend.entity;

import com.clothingstore.backend.entity.enums.SizeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Sizes", uniqueConstraints = {
        @UniqueConstraint(name = "unique_size_name_type", columnNames = {"name", "type"})
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE Sizes SET deleted_at = CURRENT_TIMESTAMP WHERE size_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Size extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "size_id")
    private String id;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SizeType type;
}
