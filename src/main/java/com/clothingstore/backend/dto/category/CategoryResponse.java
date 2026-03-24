package com.clothingstore.backend.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private String slug;
    private String parentId;
    private String image;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CategoryResponse> children;
}