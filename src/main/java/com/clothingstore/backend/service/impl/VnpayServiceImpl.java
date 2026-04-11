package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.config.VnpayProperties;
import com.clothingstore.backend.entity.Order;
import com.clothingstore.backend.entity.Payment;
import com.clothingstore.backend.entity.enums.PaymentMethod;
import com.clothingstore.backend.entity.enums.PaymentStatus;
import com.clothingstore.backend.repository.OrderRepository;
import com.clothingstore.backend.repository.PaymentRepository;
import com.clothingstore.backend.service.VnpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VnpayServiceImpl implements VnpayService {

    private static final String VERSION = "2.1.0";
    private static final String COMMAND = "pay";
    private static final String ORDER_TYPE = "other";
    private static final String CURRENCY = "VND";
    private static final String LOCALE = "vn";
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNP_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VnpayProperties vnpayProperties;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public String createPaymentUrl(String orderId, String clientIp, String userId) {
        if (!StringUtils.hasText(vnpayProperties.getTmnCode()) || !StringUtils.hasText(vnpayProperties.getHashSecret())) {
            throw new RuntimeException("VNPay is not configured (payment.vnpay.tmn-code, payment.vnpay.hash-secret)");
        }
        if (!StringUtils.hasText(vnpayProperties.getReturnUrl())) {
            throw new RuntimeException("payment.vnpay.return-url must point to /api/v1/payments/callback/vnpay");
        }

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found or access denied"));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        if (payment.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw new RuntimeException("Order payment method is not VNPAY");
        }
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Order is already paid");
        }

        BigDecimal total = order.getTotalAmount();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid order amount");
        }
        // ×100 VND, làm tròn số nguyên (tránh ArithmeticException từ longValueExact)
        long amountVnp = total.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValue();

        ZonedDateTime now = ZonedDateTime.now(VN_ZONE);
        String createDate = now.format(VNP_DATE);
        String expireDate = now.plusMinutes(15).format(VNP_DATE);

        String safeIp = StringUtils.hasText(clientIp) ? clientIp : "127.0.0.1";
        if (safeIp.length() > 64) {
            safeIp = safeIp.substring(0, 64);
        }

        TreeMap<String, String> params = new TreeMap<>();
        params.put("vnp_Amount", String.valueOf(amountVnp));
        params.put("vnp_Command", COMMAND);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_CurrCode", CURRENCY);
        params.put("vnp_ExpireDate", expireDate);
        params.put("vnp_IpAddr", safeIp);
        params.put("vnp_Locale", LOCALE);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderCode());
        params.put("vnp_OrderType", ORDER_TYPE);
        params.put("vnp_ReturnUrl", vnpayProperties.getReturnUrl());
        params.put("vnp_TmnCode", vnpayProperties.getTmnCode());
        params.put("vnp_TxnRef", order.getId());
        params.put("vnp_Version", VERSION);

        String signData = buildHashDataV210(params);
        String secureHash = hmacSha512Hex(vnpayProperties.getHashSecret(), signData);
        params.put("vnp_SecureHash", secureHash);

        return buildPaymentQueryUrl(vnpayProperties.getPayUrl(), params);
    }

    @Override
    public boolean verifyReturnParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return false;
        }
        String received = params.get("vnp_SecureHash");
        if (!StringUtils.hasText(received) || !StringUtils.hasText(vnpayProperties.getHashSecret())) {
            return false;
        }

        TreeMap<String, String> signMap = new TreeMap<>();
        for (Map.Entry<String, String> e : params.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (k != null && k.startsWith("vnp_") && !"vnp_SecureHash".equals(k)
                    && v != null && !v.isEmpty()) {
                signMap.put(k, v);
            }
        }
        String signData = buildHashDataV210(signMap);
        String calculated = hmacSha512Hex(vnpayProperties.getHashSecret(), signData);
        return received.equalsIgnoreCase(calculated);
    }

    /**
     * VNPAY 2.1.0: {@code urlencode(key)}={@code urlencode(value)}&... (mẫu PHP trên sandbox).
     */
    private static String buildHashDataV210(TreeMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (v == null || v.isEmpty()) {
                continue;
            }
            if (!first) {
                sb.append('&');
            }
            first = false;
            sb.append(vnpUrlEncode(k)).append('=').append(vnpUrlEncode(v));
        }
        return sb.toString();
    }

    private static String vnpUrlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String buildPaymentQueryUrl(String base, TreeMap<String, String> params) {
        StringBuilder q = new StringBuilder(base);
        q.append(base.contains("?") ? "&" : "?");
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!first) {
                q.append('&');
            }
            first = false;
            q.append(vnpUrlEncode(e.getKey())).append('=').append(vnpUrlEncode(e.getValue()));
        }
        return q.toString();
    }

    private static String hmacSha512Hex(String secretKey, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(key);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("VNPay HMAC failed", e);
        }
    }
}
