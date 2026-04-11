package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.config.LoyaltyProperties;
import com.clothingstore.backend.entity.LoyaltyTransaction;
import com.clothingstore.backend.entity.Order;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.entity.enums.LoyaltyTransactionType;
import com.clothingstore.backend.entity.enums.OrderStatus;
import com.clothingstore.backend.entity.enums.TierLevel;
import com.clothingstore.backend.repository.LoyaltyTransactionRepository;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final UserRepository userRepository;
    private final LoyaltyProperties loyaltyProperties;

    @Override
    @Transactional
    public void onOrderStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        if (previousStatus == newStatus || order.getUser() == null) {
            return;
        }

        boolean reachedDelivered = newStatus == OrderStatus.DELIVERED && previousStatus != OrderStatus.DELIVERED;
        boolean leftDelivered = previousStatus == OrderStatus.DELIVERED && newStatus != OrderStatus.DELIVERED;

        if (reachedDelivered) {
            earnForDelivered(order);
        } else if (leftDelivered) {
            reverseForOrder(order);
        }
    }

    private void earnForDelivered(Order order) {
        String orderId = order.getId();
        if (loyaltyTransactionRepository.existsByOrder_IdAndTransactionType(orderId, LoyaltyTransactionType.EARN_DELIVERED)) {
            return;
        }

        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal vndPerPoint = loyaltyProperties.getVndPerPoint();
        if (vndPerPoint == null || vndPerPoint.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        int points = total.divide(vndPerPoint, 0, RoundingMode.DOWN).intValue();
        if (points <= 0) {
            return;
        }

        User user = userRepository.findById(order.getUser().getId()).orElseThrow();
        int newBalance = (user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0) + points;
        user.setLoyaltyPoints(newBalance);
        user.setTierLevel(tierForPoints(newBalance));
        userRepository.save(user);

        LoyaltyTransaction tx = LoyaltyTransaction.builder()
                .user(user)
                .order(order)
                .transactionType(LoyaltyTransactionType.EARN_DELIVERED)
                .points(points)
                .orderTotalAmount(total)
                .build();
        loyaltyTransactionRepository.save(tx);
    }

    private void reverseForOrder(Order order) {
        String orderId = order.getId();
        if (loyaltyTransactionRepository.existsByOrder_IdAndTransactionType(orderId, LoyaltyTransactionType.REVERSE)) {
            return;
        }

        var earnOpt = loyaltyTransactionRepository.findByOrder_IdAndTransactionType(orderId, LoyaltyTransactionType.EARN_DELIVERED);
        if (earnOpt.isEmpty()) {
            return;
        }

        int earned = earnOpt.get().getPoints();
        User user = userRepository.findById(order.getUser().getId()).orElseThrow();
        int newBalance = Math.max(0, (user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0) - earned);
        user.setLoyaltyPoints(newBalance);
        user.setTierLevel(tierForPoints(newBalance));
        userRepository.save(user);

        LoyaltyTransaction tx = LoyaltyTransaction.builder()
                .user(user)
                .order(order)
                .transactionType(LoyaltyTransactionType.REVERSE)
                .points(earned)
                .orderTotalAmount(order.getTotalAmount())
                .build();
        loyaltyTransactionRepository.save(tx);
    }

    private TierLevel tierForPoints(int points) {
        if (points >= loyaltyProperties.getTierDiamondMinPoints()) {
            return TierLevel.DIAMOND;
        }
        if (points >= loyaltyProperties.getTierGoldMinPoints()) {
            return TierLevel.GOLD;
        }
        if (points >= loyaltyProperties.getTierSilverMinPoints()) {
            return TierLevel.SILVER;
        }
        return TierLevel.BRONZE;
    }
}
