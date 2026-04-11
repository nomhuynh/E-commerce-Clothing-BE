package com.clothingstore.backend.entity;

import com.clothingstore.backend.entity.enums.LoyaltyTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "loyalty_transactions")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoyaltyTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "loyalty_transaction_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 32)
    private LoyaltyTransactionType transactionType;

    /** Số điểm (luôn dương; cộng hay trừ tài khoản tùy transactionType) */
    @Column(name = "points", nullable = false)
    private Integer points;

    /** Giá trị đơn dùng để tính điểm (audit) */
    @Column(name = "order_total_amount", precision = 15, scale = 2)
    private BigDecimal orderTotalAmount;
}
