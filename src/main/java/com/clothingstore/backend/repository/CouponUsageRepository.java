package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.CouponUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, String> {

    @EntityGraph(attributePaths = {"user"})
    Page<CouponUsage> findByCoupon_IdOrderByUsedAtDesc(String couponId, Pageable pageable);

    long countByCoupon_IdAndUser_Id(String couponId, String userId);

    Optional<CouponUsage> findByOrderId(String orderId);
}
