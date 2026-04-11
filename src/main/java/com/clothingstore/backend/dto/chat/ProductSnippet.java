package com.clothingstore.backend.dto.chat;

import java.math.BigDecimal;

public record ProductSnippet(
        String productId,
        String name,
        String colorName,
        BigDecimal price,
        String imageUrl) {
}
