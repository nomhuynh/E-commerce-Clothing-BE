package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Promotion;

import java.util.List;

public interface PromotionService {
    Promotion create(Promotion promotion);
    Promotion update(Promotion promotion);
    Promotion getById(String id);
    List<Promotion> getAll();
    void delete(String id);
}
