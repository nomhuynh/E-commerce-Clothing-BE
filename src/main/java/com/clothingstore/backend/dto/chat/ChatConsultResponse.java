package com.clothingstore.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConsultResponse {

    private String reply;
    @Builder.Default
    private List<ProductSnippet> suggestedProducts = new ArrayList<>();
}
