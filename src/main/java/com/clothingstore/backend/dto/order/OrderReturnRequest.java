package com.clothingstore.backend.dto.order;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderReturnRequest {
    @Size(max = 500)
    private String reason;
}
