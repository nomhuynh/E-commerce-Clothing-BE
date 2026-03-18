package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Coupon;
import com.clothingstore.backend.repository.CouponRepository;
import com.clothingstore.backend.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public Coupon create(Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon update(Coupon coupon) {
        if (coupon.getId() == null) {
            throw new RuntimeException("Coupon id is required for update");
        }
        if (!couponRepository.existsById(coupon.getId())) {
            throw new RuntimeException("Coupon not found");
        }
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon getById(String id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    public Coupon getByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!couponRepository.existsById(id)) {
            throw new RuntimeException("Coupon not found");
        }
        couponRepository.deleteById(id);
    }
}
