package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, String> {
    Optional<Wishlist> findByUserIdAndProductId(String userId, String productId);

    List<Wishlist> findByUserId(String userId);

    @Query("""
            SELECT w FROM Wishlist w
            JOIN FETCH w.product p
            LEFT JOIN FETCH p.category
            WHERE w.user.id = :userId
            ORDER BY w.createdAt DESC
            """)
    List<Wishlist> findByUserIdWithProductOrdered(@Param("userId") String userId);
}
