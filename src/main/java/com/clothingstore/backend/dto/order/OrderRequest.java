package com.clothingstore.backend.dto.order;

import com.clothingstore.backend.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String customerName;
    @NotBlank
    private String customerPhone;
    @NotBlank
    private String shippingAddress;
    private String note;
    @NotNull
    private PaymentMethod paymentMethod;
}
