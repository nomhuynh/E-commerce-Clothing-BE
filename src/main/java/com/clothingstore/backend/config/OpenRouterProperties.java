package com.clothingstore.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenRouter unified API — fallback khi Gemini (và tùy cấu hình, sau Ollama).
 *
 * @see <a href="https://openrouter.ai/docs/quickstart">OpenRouter Quickstart</a>
 */
@Data
@ConfigurationProperties(prefix = "app.openrouter")
public class OpenRouterProperties {

    private boolean fallbackEnabled = true;

    /**
     * API key từ <a href="https://openrouter.ai/">OpenRouter</a>.
     */
    private String apiKey = "";

    /**
     * Model id, ví dụ {@code openai/gpt-4o-mini}, {@code google/gemini-2.0-flash-001}.
     */
    private String model = "google/gemma-4-31b-it:free";

    /**
     * Tùy chọn — header HTTP-Referer (leaderboard OpenRouter).
     */
    private String siteUrl = "";

    /**
     * Tùy chọn — header X-OpenRouter-Title.
     */
    private String siteName = "";

    public boolean isFallbackReady() {
        return fallbackEnabled
                && apiKey != null
                && !apiKey.isBlank()
                && model != null
                && !model.isBlank();
    }
}
