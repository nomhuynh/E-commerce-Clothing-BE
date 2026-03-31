package com.clothingstore.backend.entity;

import com.clothingstore.backend.entity.enums.DevicePlatform;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "DeviceTokens", uniqueConstraints = {
        @UniqueConstraint(name = "uk_device_token_user_token", columnNames = {"user_id", "token"})
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeviceToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "device_token_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private DevicePlatform platform = DevicePlatform.WEB;

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
