package com.clothingstore.backend.service.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingIntentDetectorTest {

    @Test
    void greetingsSkipSearch() {
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("Hi")).isFalse();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("Hello")).isFalse();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("Hi bot")).isFalse();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("chào shop")).isFalse();
    }

    @Test
    void fashionQuestionsRunSearch() {
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("Cho mình áo thun nam")).isTrue();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("Quần jean dưới 500k")).isTrue();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("gợi ý outfit đi làm")).isTrue();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("có sneaker size 42 không")).isTrue();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("váy maxi màu be")).isTrue();
    }

    @Test
    void metaQuestionsSkipSearch() {
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("Bạn là ai")).isFalse();
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("What can you do")).isFalse();
    }

    @Test
    void shoppingWinsOverAmbiguous() {
        assertThat(ShoppingIntentDetector.shouldRunProductSearch("mua áo ship về hn")).isTrue();
    }
}
