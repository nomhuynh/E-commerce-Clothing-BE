package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.material " +
           "WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:materialId IS NULL OR p.material.id = :materialId) " +
           "AND (:gender IS NULL OR p.gender = :gender) " +
           "AND (:ageGroup IS NULL OR p.ageGroup = :ageGroup) " +
           "AND (:minPrice IS NULL OR p.basePrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
    Page<Product> filter(
        @Param("categoryId") String categoryId,
        @Param("materialId") String materialId,
        @Param("gender") ProductGender gender,
        @Param("ageGroup") AgeGroup ageGroup,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        Pageable pageable
    );
}