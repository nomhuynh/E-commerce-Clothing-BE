package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Promotion;
import com.clothingstore.backend.repository.PromotionRepository;
import com.clothingstore.backend.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public Promotion create(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion update(Promotion promotion) {
        if (promotion.getId() == null) {
            throw new RuntimeException("Promotion id is required for update");
        }
        if (!promotionRepository.existsById(promotion.getId())) {
            throw new RuntimeException("Promotion not found");
        }
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion getById(String id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    @Override
    public List<Promotion> getAll() {
        return promotionRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Promotion not found");
        }
        promotionRepository.deleteById(id);
    }
}
