package com.clothingstore.backend.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConsultRequest {

    /** Gửi từ client để đồng bộ UI; server không lưu. */
    @JsonProperty("session_id")
    private String sessionId;

    @NotBlank(message = "message is required")
    private String message;
}
