package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, String> {
    Optional<Wishlist> findByUserIdAndProductId(String userId, String productId);
    List<Wishlist> findByUserId(String userId);
}
