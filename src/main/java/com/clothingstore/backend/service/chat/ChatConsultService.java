package com.clothingstore.backend.service.chat;

import com.clothingstore.backend.client.GeminiApiClient;
import com.clothingstore.backend.dto.chat.ChatConsultRequest;
import com.clothingstore.backend.dto.chat.ChatConsultResponse;
import com.clothingstore.backend.dto.chat.IntentExtractionResult;
import com.clothingstore.backend.dto.chat.ProductSnippet;
import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Tư vấn chat qua Gemini API. Chỉ truy vấn kho khi {@link ShoppingIntentDetector} nhận diện ý định tìm đồ / thời trang.
 */
@Service
@RequiredArgsConstructor
public class ChatConsultService {

    private static final String EXTRACTION_SYSTEM = """
            Bạn là API trích xuất JSON (tiếng Việt). Chỉ trả về MỘT object JSON hợp lệ, không markdown, không giải thích.

            Trường:
            - "keywords": mảng từ/cụm để tìm TÊN/MÔ TẢ sản phẩm. QUAN TRỌNG:
              • Cụ thể: "đồ tập gym cho nam" → ["gym","thể thao","tập","thể dục"] và từ loại đồ (vd "áo thể thao","quần tập") — KHÔNG chỉ ["áo"] hoặc ["quần"] vì quá chung (lẫn đồ trẻ em/nữ).
              • "áo sơ mi nam" → ["sơ mi","áo sơ mi"] (giới đặt ở gender; tránh từ khóa chỉ "áo").
              • gym ↔ thể thao, tập; jean ↔ denim.
            - "colors": mảng màu hoặc [].
            - "max_price": số đồng hoặc null.
            - "gender": "MALE" | "FEMALE" | "UNISEX" | null.
              MALE: nam, ông, anh, chồng, bố, đồ nam, cho trai…
              FEMALE: nữ, bà, chị, vợ, mẹ, đồ nữ…
              null nếu không rõ.
            - "age_group": "ADULT" | "TEEN" | "KID" | "BABY" | null.
              ADULT: người lớn (nam/nữ không kèm trẻ em). KID: trẻ em, bé, kids. TEEN: teen. BABY: sơ sinh. null nếu không rõ.

            Ví dụ: "gợi ý đồ tập gym cho nam" → gender MALE, age_group ADULT, keywords có gym/thể thao/tập.

            Định dạng: { "keywords": [], "colors": [], "max_price": null, "gender": null, "age_group": null }""";

    private static final String CONSULTANT_SYSTEM = """
            Bạn là nhân viên tư vấn shop thời trang Stylex. Xưng 'mình', gọi 'bạn'. Không bịa sản phẩm hay giá.

            [Dữ liệu hệ thống tìm được] là danh sách đã lọc theo truy vấn (từ khóa + giới/độ tuổi nếu có). Rỗng = không có SP phù hợp.

            Quy tắc:
            - Chỉ nói về SP trong danh sách. Không thêm SP không có trong danh sách.
            - Khách hỏi loại cụ thể (gym, sơ mi, jean…) mà danh sách trống hoặc SP không đúng loại/đối tượng → nói thẳng chưa có trong kho; không gợi ý đồ trẻ em/thắt lưng/túi như thể thay thế hợp lệ.
            - Có SP đúng nhu cầu → giới thiệu ngắn gọn.""";

    private static final String CASUAL_ASSISTANT_SYSTEM = """
            Bạn là trợ lý phong cách Stylex trên shop thời trang. Trả lời tự nhiên, thân thiện, xưng mình gọi bạn.
            Nếu khách chào hỏi, hỏi chuyện hoặc chưa nói nhu cầu mua đồ, hãy trò chuyện ngắn và mời họ mô tả (dịp mặc, loại đồ, ngân sách, màu/size) để bạn gợi ý sản phẩm sau.
            Không liệt kê sản phẩm cụ thể hay giá trong lượt hội thoại này — vì hệ thống chưa tra kho.""";

    private final GeminiApiClient geminiApiClient;
    private final IntentExtractionParser intentExtractionParser;
    private final ProductCatalogSearchService productCatalogSearchService;

    public ChatConsultResponse consult(ChatConsultRequest request) {
        String userMessage = request.getMessage().trim();
        if (userMessage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message must not be blank");
        }

        if (!ShoppingIntentDetector.shouldRunProductSearch(userMessage)) {
            return consultCasualOnly(userMessage);
        }

        return consultWithProductSearch(userMessage);
    }

    private ChatConsultResponse consultCasualOnly(String userMessage) {
        try {
            String reply = geminiApiClient.chatSingleTurn(CASUAL_ASSISTANT_SYSTEM, "Khách: " + userMessage);
            return ChatConsultResponse.builder()
                    .reply(reply)
                    .suggestedProducts(Collections.emptyList())
                    .build();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI failed: " + e.getMessage(), e);
        }
    }

    /** Số SP tối đa lấy từ DB để prompt AI so sánh / chọn (≥ số thẻ hiển thị). */
    private static final int PRODUCT_SEARCH_LIMIT = 10;
    private static final int MAX_SUGGESTED_CARDS = 5;

    private ChatConsultResponse consultWithProductSearch(String userMessage) {
        String extractionUserPrompt = "Câu hỏi: " + userMessage;
        IntentExtractionResult intent;
        try {
            String rawJson = geminiApiClient.chatSingleTurn(EXTRACTION_SYSTEM, extractionUserPrompt);
            intent = intentExtractionParser.parse(rawJson);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI extraction failed: " + e.getMessage(), e);
        }

        List<ProductSnippet> products = productCatalogSearchService.search(intent, PRODUCT_SEARCH_LIMIT);
        String productBlock = formatProductsForPrompt(intent, products);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", CONSULTANT_SYSTEM));
        String filterLine = describeAppliedFilters(intent);
        String userTurn = userMessage
                + (filterLine.isEmpty() ? "" : "\n\n[Bộ lọc hệ thống đã áp dụng]: " + filterLine)
                + "\n\n[Dữ liệu hệ thống tìm được]: "
                + productBlock;
        messages.add(Map.of("role", "user", "content", userTurn));

        List<ProductSnippet> forCards = products.size() <= MAX_SUGGESTED_CARDS
                ? products
                : products.subList(0, MAX_SUGGESTED_CARDS);
        try {
            String reply = geminiApiClient.chatMessages(messages);
            return ChatConsultResponse.builder()
                    .reply(reply)
                    .suggestedProducts(forCards)
                    .build();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI consultant failed: " + e.getMessage(), e);
        }
    }

    private static String describeAppliedFilters(IntentExtractionResult intent) {
        List<String> parts = new ArrayList<>();
        if (intent.genderFilter() != null) {
            parts.add(switch (intent.genderFilter()) {
                case MALE -> "giới tính: nam (MALE hoặc unisex)";
                case FEMALE -> "giới tính: nữ (FEMALE hoặc unisex)";
                case UNISEX -> "giới tính: unisex";
            });
        }
        if (intent.ageGroupFilter() != null) {
            parts.add(switch (intent.ageGroupFilter()) {
                case ADULT -> "độ tuổi: người lớn (ADULT)";
                case TEEN -> "độ tuổi: teen";
                case KID -> "độ tuổi: trẻ em";
                case BABY -> "độ tuổi: em bé";
            });
        }
        return String.join("; ", parts);
    }

    private static String formatProductsForPrompt(IntentExtractionResult intent, List<ProductSnippet> products) {
        if (products.isEmpty()) {
            return "(Không tìm thấy sản phẩm phù hợp trong kho.)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(Các dòng dưới đây đã khớp từ khóa");
        if (intent.genderFilter() != null || intent.ageGroupFilter() != null) {
            sb.append(" và bộ lọc giới/độ tuổi");
        }
        sb.append(")\n");
        for (ProductSnippet p : products) {
            String color = p.colorName() != null ? p.colorName() : "N/A";
            sb.append("- ")
                    .append(p.name())
                    .append(" - ")
                    .append(color)
                    .append(" - ")
                    .append(formatVnd(p.price()))
                    .append('\n');
        }
        return sb.toString().trim();
    }

    private static String formatVnd(BigDecimal price) {
        if (price == null) {
            return "0 ₫";
        }
        return String.format("%,.0f ₫", price);
    }
}
