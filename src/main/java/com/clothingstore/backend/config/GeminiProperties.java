package com.clothingstore.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.gemini")
public class GeminiProperties {

    /**
     * API key từ <a href="https://aistudio.google.com/app/apikey">Google AI Studio</a>.
     * Có thể đặt biến môi trường GEMINI_API_KEY thay cho property (Client mặc định đọc env).
     */
    private String apiKey = "";

    /**
     * Model id, ví dụ gemini-2.0-flash, gemini-2.5-flash.
     */
    private String model = "gemini-2.0-flash";
}
