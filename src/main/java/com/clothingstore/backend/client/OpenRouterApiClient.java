package com.clothingstore.backend.client;

import com.clothingstore.backend.config.OpenRouterProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenRouter — {@code POST /api/v1/chat/completions} (tương thích OpenAI).
 *
 * @see <a href="https://openrouter.ai/docs/quickstart">OpenRouter Quickstart</a>
 */
@Component
@RequiredArgsConstructor
public class OpenRouterApiClient {

    private static final String BASE_URL = "https://openrouter.ai/api/v1";

    private final OpenRouterProperties openRouterProperties;
    private final ObjectMapper objectMapper;

    public String chatSingleTurn(String systemPrompt, String userPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userPrompt));
        return chatCompletions(messages);
    }

    public String chatCompletions(List<Map<String, String>> messages) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", openRouterProperties.getModel());
            body.put("messages", messages);
            body.put("stream", false);
            String payload = objectMapper.writeValueAsString(body);

            RestClient client = RestClient.builder().baseUrl(BASE_URL).build();
            var spec = client.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openRouterProperties.getApiKey().trim());
            String referer = openRouterProperties.getSiteUrl();
            if (referer != null && !referer.isBlank()) {
                spec = spec.header("HTTP-Referer", referer.trim());
            }
            String title = openRouterProperties.getSiteName();
            if (title != null && !title.isBlank()) {
                spec = spec.header("X-OpenRouter-Title", title.trim());
            }
            String raw = spec.body(payload).retrieve().body(String.class);
            if (raw == null || raw.isBlank()) {
                throw new IllegalStateException("Empty response from OpenRouter");
            }
            JsonNode root = objectMapper.readTree(raw);
            JsonNode err = root.path("error");
            if (!err.isMissingNode() && !err.isNull()) {
                String em = err.path("message").asText(err.toString());
                throw new IllegalStateException("OpenRouter API error: " + em);
            }
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            if (content.isBlank()) {
                throw new IllegalStateException("OpenRouter returned empty message content");
            }
            return content;
        } catch (RestClientException e) {
            throw new IllegalStateException("OpenRouter request failed: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("OpenRouter response handling failed: " + e.getMessage(), e);
        }
    }
}
