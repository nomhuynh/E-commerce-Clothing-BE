package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String>, JpaSpecificationExecutor<Promotion> {
}
