package com.clothingstore.backend.client;

import com.clothingstore.backend.config.OllamaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ollama HTTP API ({@code POST /api/chat}) — dùng trong chuỗi fallback khi Gemini rate limit.
 * Ollama Cloud: {@code https://ollama.com} + Bearer API key.
 */
@Component
@RequiredArgsConstructor
public class OllamaApiClient {

    private final OllamaProperties ollamaProperties;
    private final ObjectMapper objectMapper;

    public String chatSingleTurn(String systemPrompt, String userPrompt) {
        return chatMessages(
                List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)));
    }

    public String chatMessages(List<Map<String, String>> messages) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", ollamaProperties.getModel());
            body.put("messages", messages);
            body.put("stream", false);
            String payload = objectMapper.writeValueAsString(body);

            RestClient client = RestClient.builder().baseUrl(trimBaseUrl(ollamaProperties.getBaseUrl())).build();

            var spec = client.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON);
            if (ollamaProperties.getApiKey() != null && !ollamaProperties.getApiKey().isBlank()) {
                spec = spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + ollamaProperties.getApiKey().trim());
            }
            String raw = spec.body(payload).retrieve().body(String.class);
            if (raw == null || raw.isBlank()) {
                throw new IllegalStateException("Empty response from Ollama");
            }
            JsonNode root = objectMapper.readTree(raw);
            String content = root.path("message").path("content").asText("");
            if (content.isBlank()) {
                throw new IllegalStateException("Ollama returned empty message.content");
            }
            return content;
        } catch (RestClientException e) {
            throw new IllegalStateException("Ollama request failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Ollama response handling failed: " + e.getMessage(), e);
        }
    }

    /** @see OllamaProperties#isFallbackReady() */
    public boolean isConfigured() {
        return ollamaProperties.isFallbackReady();
    }

    private static String trimBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://ollama.com";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
