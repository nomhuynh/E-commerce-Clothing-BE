package com.clothingstore.backend.service.chat;

import com.clothingstore.backend.dto.chat.IntentExtractionResult;
import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Trích xuất JSON intent từ phản hồi model (có thể kèm text thừa); dùng substring từ { đến } cuối.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IntentExtractionParser {

    private final ObjectMapper objectMapper;

    public IntentExtractionResult parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return IntentExtractionResult.empty();
        }
        try {
            String json = extractJsonObject(raw);
            JsonNode root = objectMapper.readTree(json);
            List<String> keywords = readStringArray(root.path("keywords"));
            List<String> colors = readStringArray(root.path("colors"));
            BigDecimal maxPrice = readMaxPrice(root.path("max_price"));
            ProductGender genderFilter = readGender(root.path("gender"));
            AgeGroup ageGroupFilter = readAgeGroup(root.path("age_group"));
            return new IntentExtractionResult(keywords, colors, maxPrice, genderFilter, ageGroupFilter);
        } catch (Exception e) {
            log.warn("Intent JSON parse failed, using empty intent: {}", e.getMessage());
            return IntentExtractionResult.empty();
        }
    }

    static String extractJsonObject(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalArgumentException("No JSON object in model output");
        }
        return raw.substring(start, end + 1);
    }

    private static List<String> readStringArray(JsonNode node) {
        List<String> out = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode n : node) {
                if (n.isTextual()) {
                    String s = n.asText().trim();
                    if (!s.isEmpty()) {
                        out.add(s);
                    }
                }
            }
        }
        return out;
    }

    private static BigDecimal readMaxPrice(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isNumber()) {
            return node.decimalValue();
        }
        if (node.isTextual()) {
            String t = node.asText().trim();
            if (t.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(t);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static ProductGender readGender(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        String t = node.asText("").trim().toUpperCase();
        if (t.isEmpty()) {
            return null;
        }
        try {
            return ProductGender.valueOf(t);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static AgeGroup readAgeGroup(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        String t = node.asText("").trim().toUpperCase();
        if (t.isEmpty()) {
            return null;
        }
        try {
            return AgeGroup.valueOf(t);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
