package com.clothingstore.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "payment.vnpay")
public class VnpayProperties {

    private String tmnCode = "";
    private String hashSecret = "";
    private String payUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    /** Backend URL that VNPay redirects to (must be publicly reachable for sandbox). */
    private String returnUrl = "";
}
