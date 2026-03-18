package com.clothingstore.backend.dto.address;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String recipientName;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String streetAddress;

    @NotBlank
    private String ward;

    @NotBlank
    private String district;

    @NotBlank
    private String city;

    private Boolean isDefault;
}
