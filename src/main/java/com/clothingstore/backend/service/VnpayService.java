package com.clothingstore.backend.service;

import java.util.Map;

public interface VnpayService {

    /**
     * Builds signed VNPay payment URL for the order (amount × 100 VND, vnp_TxnRef = order id).
     */
    String createPaymentUrl(String orderId, String clientIp, String userId);

    /** Verifies {@code vnp_SecureHash} on the return query string from VNPay. */
    boolean verifyReturnParams(Map<String, String> params);
}
