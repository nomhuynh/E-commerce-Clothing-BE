package com.clothingstore.backend.service.chat;

import com.clothingstore.backend.dto.chat.IntentExtractionResult;
import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class IntentExtractionParserTest {

    private final IntentExtractionParser parser = new IntentExtractionParser(new ObjectMapper());

    @Test
    void parse_extractsFromNoisyModelOutput() {
        String raw = """
                Đây là JSON:
                { "keywords": ["áo thun", "quần jean"], "colors": ["đen"], "max_price": 500000 }
                Hết.
                """;
        IntentExtractionResult r = parser.parse(raw);
        assertThat(r.keywords()).containsExactly("áo thun", "quần jean");
        assertThat(r.colors()).containsExactly("đen");
        assertThat(r.maxPrice()).isEqualByComparingTo(new BigDecimal("500000"));
        assertThat(r.genderFilter()).isNull();
        assertThat(r.ageGroupFilter()).isNull();
    }

    @Test
    void parse_readsGenderAndAgeGroup() {
        String raw = """
                { "keywords": ["gym"], "colors": [], "max_price": null, "gender": "MALE", "age_group": "ADULT" }
                """;
        IntentExtractionResult r = parser.parse(raw);
        assertThat(r.genderFilter()).isEqualTo(ProductGender.MALE);
        assertThat(r.ageGroupFilter()).isEqualTo(AgeGroup.ADULT);
    }

    @Test
    void parse_returnsEmptyOnInvalidJson() {
        IntentExtractionResult r = parser.parse("no braces here");
        assertThat(r.keywords()).isEmpty();
        assertThat(r.colors()).isEmpty();
        assertThat(r.maxPrice()).isNull();
        assertThat(r.genderFilter()).isNull();
        assertThat(r.ageGroupFilter()).isNull();
    }
}
