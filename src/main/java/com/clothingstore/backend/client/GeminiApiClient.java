package com.clothingstore.backend.client;

import com.clothingstore.backend.config.GeminiProperties;
import com.clothingstore.backend.config.OllamaProperties;
import com.clothingstore.backend.config.OpenRouterProperties;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Chuỗi LLM: <strong>Gemini</strong> (primary) → <strong>Ollama</strong> → <strong>OpenRouter</strong>
 * khi rate limit / quota / 404 (tùy cấu hình).
 *
 * @see <a href="https://openrouter.ai/docs/quickstart">OpenRouter Quickstart</a>
 */
@Slf4j
@Component
public class GeminiApiClient {

    private final GeminiProperties geminiProperties;
    private final OllamaProperties ollamaProperties;
    private final OpenRouterProperties openRouterProperties;
    private final OllamaApiClient ollamaApiClient;
    private final OpenRouterApiClient openRouterApiClient;
    private final Client client;

    public GeminiApiClient(
            GeminiProperties geminiProperties,
            OllamaProperties ollamaProperties,
            OpenRouterProperties openRouterProperties,
            OllamaApiClient ollamaApiClient,
            OpenRouterApiClient openRouterApiClient) {
        this.geminiProperties = geminiProperties;
        this.ollamaProperties = ollamaProperties;
        this.openRouterProperties = openRouterProperties;
        this.ollamaApiClient = ollamaApiClient;
        this.openRouterApiClient = openRouterApiClient;
        String key = geminiProperties.getApiKey();
        this.client = (key != null && !key.isBlank())
                ? Client.builder().apiKey(key.trim()).build()
                : new Client();
    }

    @PreDestroy
    public void close() {
        client.close();
    }

    public String chatSingleTurn(String systemPrompt, String userPrompt) {
        try {
            return geminiGenerate(systemPrompt, userPrompt);
        } catch (Exception e) {
            if (!shouldTryFallbackAfterGeminiFailure(e)) {
                throw new IllegalStateException("Gemini request failed: " + e.getMessage(), e);
            }
            return runFallbackChain(systemPrompt, userPrompt, e);
        }
    }

    private String runFallbackChain(String systemPrompt, String userPrompt, Exception geminiError) {
        if (ollamaProperties.isFallbackReady()) {
            try {
                log.warn("Gemini failed ({}); trying Ollama fallback", summary(geminiError));
                return ollamaApiClient.chatSingleTurn(systemPrompt, userPrompt);
            } catch (Exception ex) {
                log.warn("Ollama fallback failed: {}", ex.getMessage());
            }
        }
        if (openRouterProperties.isFallbackReady()) {
            try {
                log.warn("Trying OpenRouter fallback after Gemini error: {}", summary(geminiError));
                return openRouterApiClient.chatSingleTurn(systemPrompt, userPrompt);
            } catch (Exception ex) {
                log.warn("OpenRouter fallback failed: {}", ex.getMessage());
            }
        }
        throw new IllegalStateException(
                "Gemini failed and no working fallback (Ollama/OpenRouter): " + geminiError.getMessage(),
                geminiError);
    }

    /**
     * Hỗ trợ chuỗi system + user. Role đầu là system, các phần còn lại ghép làm user.
     */
    public String chatMessages(List<Map<String, String>> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalStateException("messages must not be empty");
        }
        String systemPrompt = "";
        StringBuilder userParts = new StringBuilder();
        for (Map<String, String> m : messages) {
            String role = m.getOrDefault("role", "user");
            String content = m.getOrDefault("content", "");
            if ("system".equalsIgnoreCase(role)) {
                systemPrompt = content;
            } else {
                if (userParts.length() > 0) {
                    userParts.append("\n\n");
                }
                userParts.append(content);
            }
        }
        String userText = userParts.length() > 0 ? userParts.toString() : "";
        if (userText.isBlank()) {
            throw new IllegalStateException("No user content in messages");
        }
        return chatSingleTurn(systemPrompt.isBlank() ? "You are a helpful assistant." : systemPrompt, userText);
    }

    private String geminiGenerate(String systemPrompt, String userPrompt) {
        Content systemInstruction = Content.fromParts(Part.fromText(systemPrompt));
        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(systemInstruction)
                .build();
        GenerateContentResponse response = client.models.generateContent(
                geminiProperties.getModel(),
                userPrompt,
                config);
        return requireText(response);
    }

    private static boolean shouldTryFallbackAfterGeminiFailure(Throwable e) {
        return isGeminiFallbackTrigger(e);
    }

    /**
     * Rate limit, quota, 404 (model/endpoint), v.v.
     */
    static boolean isGeminiFallbackTrigger(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            String name = t.getClass().getName();
            if (name.contains("RateLimit") || name.contains("ResourceExhausted")) {
                return true;
            }
            String msg = String.valueOf(t.getMessage()).toLowerCase();
            if (msg.contains("429")
                    || msg.contains("404")
                    || msg.contains("not found")
                    || msg.contains("resource exhausted")
                    || msg.contains("resource_exhausted")
                    || msg.contains("rate limit")
                    || msg.contains("ratelimit")
                    || msg.contains("quota")
                    || msg.contains("too many requests")) {
                return true;
            }
        }
        return false;
    }

    private static String summary(Throwable e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    private static String requireText(GenerateContentResponse response) {
        if (response == null) {
            throw new IllegalStateException("Empty response from Gemini");
        }
        String text = response.text();
        if (text == null || text.isBlank()) {
            throw new IllegalStateException("Gemini returned empty text (blocked or no candidates)");
        }
        return text;
    }
}
