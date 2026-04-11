package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.LoyaltyTransaction;
import com.clothingstore.backend.entity.enums.LoyaltyTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, String> {

    boolean existsByOrder_IdAndTransactionType(String orderId, LoyaltyTransactionType transactionType);

    Optional<LoyaltyTransaction> findByOrder_IdAndTransactionType(String orderId, LoyaltyTransactionType transactionType);
}
