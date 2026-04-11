package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("""
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.material
            LEFT JOIN FETCH p.images
            """)
    List<Product> findAllWithCategoryAndMaterial();

    @Query("""
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.material
            LEFT JOIN FETCH p.images
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Product> searchByNameContainingIgnoreCase(@Param("keyword") String keyword);

    @Query("""
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.material
            LEFT JOIN FETCH p.images
            WHERE p.id = :id
            """)
    Optional<Product> findByIdWithImages(@Param("id") String id);
}
