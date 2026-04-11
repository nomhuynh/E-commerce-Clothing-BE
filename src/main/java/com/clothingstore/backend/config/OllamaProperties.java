package com.clothingstore.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ollama dùng làm <strong>fallback</strong> khi Gemini bị rate limit / quota.
 * <p>
 * Ollama Cloud: {@code https://ollama.com} + API key (Bearer) — xem
 * <a href="https://ollama.com/blog/cloud">Ollama Cloud</a>.
 * Local: {@code http://127.0.0.1:11434}, API key tùy chọn.
 */
@Data
@ConfigurationProperties(prefix = "app.ollama")
public class OllamaProperties {

    /**
     * Bật chuỗi fallback: Gemini (primary) → Ollama khi gặp rate limit / quota.
     */
    private boolean fallbackEnabled = true;

    /**
     * Mặc định Ollama Cloud. Local: {@code http://127.0.0.1:11434}.
     */
    private String baseUrl = "https://ollama.com";

    /**
     * API key Ollama Cloud (Bearer). Bắt buộc khi {@link #isCloudHost()} là true.
     */
    private String apiKey = "";

    /**
     * Model trên cloud/local, ví dụ {@code llama3.2}, {@code gemma2:2b}, tùy gói Cloud.
     */
    private String model = "llama3.2";

    /**
     * Fallback được cấu hình đủ để gọi (URL + model; cloud thì cần thêm API key).
     */
    public boolean isFallbackReady() {
        if (!fallbackEnabled) {
            return false;
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            return false;
        }
        if (model == null || model.isBlank()) {
            return false;
        }
        if (isCloudHost()) {
            return apiKey != null && !apiKey.isBlank();
        }
        return true;
    }

    /**
     * Endpoint Ollama Cloud ({@code ollama.com}) — cần API key.
     */
    public boolean isCloudHost() {
        if (baseUrl == null) {
            return false;
        }
        String u = baseUrl.toLowerCase().trim();
        return u.contains("ollama.com");
    }
}
