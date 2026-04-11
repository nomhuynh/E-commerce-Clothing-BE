package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Coupon;
import com.clothingstore.backend.entity.CouponUsage;
import com.clothingstore.backend.repository.CouponRepository;
import com.clothingstore.backend.repository.CouponUsageRepository;
import com.clothingstore.backend.service.CouponService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

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
    public Page<Coupon> findPageForAdmin(int page, int limit, String codeSearch, Boolean isActive) {
        int p = Math.max(1, page);
        int l = Math.min(100, Math.max(1, limit));
        Specification<Coupon> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (codeSearch != null && !codeSearch.isBlank()) {
                String pattern = "%" + codeSearch.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("code")), pattern));
            }
            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return couponRepository.findAll(
                spec,
                PageRequest.of(p - 1, l, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Override
    public Page<CouponUsage> findUsagesForAdmin(String couponId, int page, int limit) {
        int p = Math.max(1, page);
        int l = Math.min(100, Math.max(1, limit));
        return couponUsageRepository.findByCoupon_IdOrderByUsedAtDesc(
                couponId,
                PageRequest.of(p - 1, l));
    }

    @Override
    public void delete(String id) {
        if (!couponRepository.existsById(id)) {
            throw new RuntimeException("Coupon not found");
        }
        couponRepository.deleteById(id);
    }
}
