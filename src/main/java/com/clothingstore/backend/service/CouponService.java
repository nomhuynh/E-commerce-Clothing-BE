package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Coupon;
import com.clothingstore.backend.entity.CouponUsage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CouponService {
    Coupon create(Coupon coupon);
    Coupon update(Coupon coupon);
    Coupon getById(String id);
    Coupon getByCode(String code);
    List<Coupon> getAll();

    Page<Coupon> findPageForAdmin(int page, int limit, String codeSearch, Boolean isActive);

    Page<CouponUsage> findUsagesForAdmin(String couponId, int page, int limit);

    void delete(String id);
}
