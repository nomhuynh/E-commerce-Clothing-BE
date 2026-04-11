package com.clothingstore.backend.entity.enums;

/**
 * Ghi nhận loại giao dịch điểm (đối soát, idempotent theo đơn).
 */
public enum LoyaltyTransactionType {
    /** Cộng điểm khi đơn chuyển sang DELIVERED */
    EARN_DELIVERED,
    /** Hoàn điểm khi rời DELIVERED (hủy sau giao, trả hàng, sửa sai trạng thái, …) */
    REVERSE
}
