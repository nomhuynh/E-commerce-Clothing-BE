package com.clothingstore.backend.dto.address;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AddressResponse {
    private String id;
    private String userId;
    private String recipientName;
    private String phoneNumber;
    private String streetAddress;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
