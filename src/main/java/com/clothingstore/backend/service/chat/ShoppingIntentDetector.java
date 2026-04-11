package com.clothingstore.backend.service.chat;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Quyết định có truy vấn kho sản phẩm hay chỉ hội thoại tự nhiên (Gemini một lượt).
 * Ưu tiên: có tín hiệu thời trang / mua đồ → search; sau đó mới lọc chào hỏi / small talk.
 */
public final class ShoppingIntentDetector {

    private ShoppingIntentDetector() {
    }

    private static final Pattern GREETING_OR_PING_ONLY = Pattern.compile(
            "(?is)^\\s*(hi|hello|hey|chào|chao|xin\\s*chào|good\\s*(morning|afternoon|evening)|test|ping|ok|okay|cảm\\s*ơn|thanks?|thank\\s*you|bye|tạm\\s*biệt|hẹn\\s*gặp|chào\\s*bạn|yo|sup)\\b[\\s!.,?]*$");

    private static final Pattern SHORT_GREETING_WITH_WORD = Pattern.compile(
            "(?is)^\\s*(hi|hello|hey|chào)\\s+\\w{1,20}\\s*[!.?]*$");

    /** Meta / không liên quan tìm đồ (chỉ khi chưa có tín hiệu shopping). */
    private static final List<Pattern> CASUAL_META_PATTERNS = List.of(
            Pattern.compile("(?is).*(bạn\\s+là\\s+ai|who\\s+are\\s+you|what\\s+can\\s+you\\s+do|bạn\\s+làm\\s+được\\s+gì).*"),
            Pattern.compile("(?is)^\\s*(help|trợ\\s*giúp)\\s*[!?.]*$"));

    private static final Pattern SHOPPING_REGEX_EXTRA = Pattern.compile(
            "(?is).*(thời\\s*trang|phối\\s*đồ|mặc\\s+đẹp|đi\\s+tiệc|đi\\s+làm|công\\s+sở|đồng\\s+hồ|kính\\s*mát).*");

    /**
     * Từ / cụm gợi ý tra kho (contains trên chuỗi lowercase).
     */
    private static final List<String> SHOPPING_SUBSTRINGS = List.of(
            "áo thun", "áo sơ mi", "áo khoác", "áo len", "áo nỉ", "áo vest", "áo blazer", "áo crop",
            "áo tank", "áo polo", "áo ba lỗ", "áo hoodie", "áo gile", "cardigan", "sơ mi", "thun",
            "quần jean", "quần tây", "quần đùi", "quần short", "quần baggy", "quần jogger", "quần legging",
            "quần suông", "skinny", "culottes", "váy", "đầm", "chân váy", "maxi", "mini",
            "jumpsuit", "yếm", "bodysuit", "đồ bộ", "set đồ", "bikini", "đồ bơi", "đồ ngủ", "đồ lót",
            "khoác", "parka", "bomber", "windbreaker", "trench", "blazer", "hoodie", "sweater",
            "giày", "dép", "sandal", "boot", "sneaker", "slipper", "loafer", "mũ", "nón", "beret",
            "túi xách", "balo", "ví", "thắt lưng", "dây nịt", "khăn", "tất", "vớ", "găng tay",
            "phụ kiện", "trang sức", "kính mát",
            "denim", "jean", "jeans", "cotton", "len", "lụa", "linen", "nỉ", "da", "kaki",
            "oversized", "ôm body", "form rộng", "slim", "rộng",
            "màu đen", "màu trắng", "màu be", "màu xám", "màu nâu", "màu đỏ", "màu xanh", "màu hồng",
            "size s", "size m", "size l", "size xl", "xxl",
            "đi làm", "công sở", "đi chơi", "đi học", "đi biển", "du lịch", "dự tiệc", "tiệc cưới",
            "phỏng vấn", "hẹn hò", "gym", "thể thao", "yoga", "chạy bộ", "leo núi", "mùa hè", "mùa đông",
            "mùa thu", "xuân hạ", "thu đông", "formal", "casual", "streetwear", "minimal", "cổ điển",
            "trẻ em", "bé trai", "bé gái", "nam giới", "nữ giới", "unisex",
            "triệu", "nghìn", "ngân sách", "dưới 1", "dưới 2", "dưới 3", "dưới 5", "dưới 10",
            "trên 1", "trên 2", "tầm ", "khoảng ", " giá ", "giá cả",
            "sale", "giảm giá", "khuyến mãi", "voucher", "combo", "deal", "hàng mới",
            "còn hàng", "best seller", "bán chạy", "xu hướng",
            "gợi ý", "tư vấn", "recommend", "suggest", "tìm giúp", "tìm cho", "có bán", "bên bạn có",
            "shop có", "cửa hàng có", "mẫu nào", "loại nào", "form nào", "phối đồ", "mix đồ", "outfit",
            "mặc gì", "mặc sao", "mặc đi", "mặc cho", "mặc với", "mặc đẹp", "mặc cho tôi",
            "mua ", "mua đồ", "mua áo", "mua quần", "đặt hàng", "order", "add to cart",
            "tôi cần", "mình cần", "em cần", "cần tìm", "đang tìm", "muốn mua", "muốn xem",
            "shirt", "t-shirt", "tee", "pants", "trousers", "dress", "skirt", "jacket", "coat",
            "shoes", "sneakers", "boots", "bag", "wallet", "watch", "fashion", "clothes", "clothing",
            "wear", "outfit", "style", "look", "trend", "price", "budget", "size", "color", "colour",
            "buy", "browse", "catalog", "collection",
            "áo ", " áo", "quần", "váy", "đầm", "đồ ", " mặc", "màu ", "size ", "giày", "túi ");

    public static boolean shouldRunProductSearch(String message) {
        String t = message.trim();
        if (t.isEmpty()) {
            return false;
        }
        String lower = t.toLowerCase(Locale.ROOT);

        // 1) Rõ ràng đang hỏi đồ / thời trang → luôn tra kho
        if (containsShoppingSignals(lower)) {
            return true;
        }

        // 2) Chào / hỏi meta / ping → không tra kho
        if (matchesCasualOnly(t, lower)) {
            return false;
        }

        // 3) Câu dài: coi như có thể mô tả nhu cầu
        if (t.length() > 42) {
            return true;
        }

        if (GREETING_OR_PING_ONLY.matcher(t).matches() || SHORT_GREETING_WITH_WORD.matcher(t).matches()) {
            return false;
        }

        return t.length() > 20;
    }

    private static boolean matchesCasualOnly(String original, String lower) {
        if (GREETING_OR_PING_ONLY.matcher(original).matches() || SHORT_GREETING_WITH_WORD.matcher(original).matches()) {
            return true;
        }
        for (Pattern p : CASUAL_META_PATTERNS) {
            if (p.matcher(original).matches()) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsShoppingSignals(String lower) {
        if (SHOPPING_REGEX_EXTRA.matcher(lower).matches()) {
            return true;
        }
        for (String s : SHOPPING_SUBSTRINGS) {
            if (lower.contains(s.trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
