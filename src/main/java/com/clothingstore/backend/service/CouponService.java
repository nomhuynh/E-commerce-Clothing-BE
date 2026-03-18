package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Coupon;

import java.util.List;

public interface CouponService {
    Coupon create(Coupon coupon);
    Coupon update(Coupon coupon);
    Coupon getById(String id);
    Coupon getByCode(String code);
    List<Coupon> getAll();
    void delete(String id);
}
