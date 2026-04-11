package com.clothingstore.backend.service.chat;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCatalogSearchServiceTest {

    @Test
    void expandFashionKeywords_addsJeanSynonyms() {
        List<String> r = ProductCatalogSearchService.expandFashionKeywords(List.of("quần jeans"));
        assertTrue(r.contains("jean"));
        assertTrue(r.contains("jeans"));
        assertTrue(r.contains("denim"));
    }
}
