package com.clothingstore.backend.dto.color;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorResponse {
    private String id;
    private String name;
    private String hexCode;
    private Instant createdAt;
    private Instant updatedAt;
}