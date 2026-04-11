package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.wishlist.WishlistRequest;
import com.clothingstore.backend.dto.wishlist.WishlistResponse;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.entity.Wishlist;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.repository.WishlistRepository;
import com.clothingstore.backend.service.ProductPromotionEnrichmentService;
import com.clothingstore.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductPromotionEnrichmentService productPromotionEnrichmentService;

    @Override
    @Transactional
    public WishlistResponse add(WishlistRequest request) {
        wishlistRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId())
                .ifPresent(w -> { throw new RuntimeException("Product already in wishlist"); });

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist wishlist = Wishlist.builder().user(user).product(product).build();
        Wishlist saved = wishlistRepository.save(wishlist);
        Product p = saved.getProduct();
        if (p.getCategory() != null) {
            p.getCategory().getName();
        }
        p.getImages().size();
        p.getVariants().size();
        productPromotionEnrichmentService.enrichProducts(List.of(p));
        return toResponse(saved);
    }

    @Override
    public void remove(String userId, String productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        wishlistRepository.delete(wishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> getByUser(String userId) {
        List<Wishlist> list = wishlistRepository.findByUserIdWithProductOrdered(userId);
        for (Wishlist w : list) {
            Product p = w.getProduct();
            p.getImages().size();
            p.getVariants().size();
        }
        productPromotionEnrichmentService.enrichProducts(list.stream().map(Wishlist::getProduct).toList());
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public boolean isInWishlist(String userId, String productId) {
        return wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        Product p = wishlist.getProduct();
        if (p.getCategory() != null) {
            p.getCategory().getName();
        }
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .productId(p.getId())
                .productName(p.getName())
                .product(p)
                .build();
    }
}
