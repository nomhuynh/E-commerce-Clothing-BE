package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.wishlist.WishlistRequest;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.repository.WishlistRepository;
import com.clothingstore.backend.service.impl.WishlistServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock private WishlistRepository wishlistRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Test
    void add_ShouldThrow_WhenProductAlreadyInWishlist() {
        WishlistRequest req = new WishlistRequest();
        req.setUserId("u1");
        req.setProductId("p1");

        when(wishlistRepository.findByUserIdAndProductId("u1", "p1"))
                .thenReturn(Optional.of(new com.clothingstore.backend.entity.Wishlist()));

        assertThrows(RuntimeException.class, () -> wishlistService.add(req));
    }
}
