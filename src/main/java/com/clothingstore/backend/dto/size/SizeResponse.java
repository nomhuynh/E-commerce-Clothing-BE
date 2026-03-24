package com.clothingstore.backend.dto.size;

import com.clothingstore.backend.entity.enums.SizeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SizeResponse {
    private String id;
    private String name;
    private SizeType type;
    private Instant createdAt;
    private Instant updatedAt;
}