package com.clothingstore.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Cấu hình tích điểm và ngưỡng hạng (điểm tích lũy trọn đời).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.loyalty")
public class LoyaltyProperties {

    /** VND mỗi 1 điểm (ví dụ 10000 = 1 điểm / 10k VND) */
    private BigDecimal vndPerPoint = new BigDecimal("10000");

    /** Từ điểm này trở lên → Silver */
    private int tierSilverMinPoints = 1000;

    /** Từ điểm này trở lên → Gold */
    private int tierGoldMinPoints = 5000;

    /** Từ điểm này trở lên → Diamond */
    private int tierDiamondMinPoints = 15000;
}
