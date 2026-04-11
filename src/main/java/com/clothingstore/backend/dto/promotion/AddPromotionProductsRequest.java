package com.clothingstore.backend.dto.promotion;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddPromotionProductsRequest {

    @JsonProperty("product_ids")
    @JsonAlias("productIds")
    private List<String> productIds;
}
