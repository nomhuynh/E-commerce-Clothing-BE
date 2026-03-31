package com.clothingstore.backend.dto.order;

import lombok.Data;

@Data
public class MyOrdersQuery {
    private Integer page = 1;
    private Integer limit = 10;
    private String status;
}
