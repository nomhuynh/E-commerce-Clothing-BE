package com.clothingstore.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminOrderStatusRequest {

    @NotBlank
    private String status;
}
