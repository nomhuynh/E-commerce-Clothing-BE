package com.clothingstore.backend.dto.size;

import com.clothingstore.backend.entity.enums.SizeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SizeRequest {
    private String name;
    private SizeType type;
}