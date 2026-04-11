package com.clothingstore.backend.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiApiClientRateLimitTest {

    @Test
    void detectsRateLimitMessages() {
        assertTrue(GeminiApiClient.isGeminiFallbackTrigger(
                new RuntimeException("HTTP 429 Too Many Requests")));
        assertTrue(GeminiApiClient.isGeminiFallbackTrigger(
                new IllegalStateException("RESOURCE_EXHAUSTED: quota")));
        assertTrue(GeminiApiClient.isGeminiFallbackTrigger(
                new Exception(new RuntimeException("rate limit exceeded"))));
    }

    @Test
    void detects404AndNotFound() {
        assertTrue(GeminiApiClient.isGeminiFallbackTrigger(new RuntimeException("HTTP 404")));
        assertTrue(GeminiApiClient.isGeminiFallbackTrigger(new IllegalStateException("model not found")));
    }

    @Test
    void ignoresUnrelatedErrors() {
        assertFalse(GeminiApiClient.isGeminiFallbackTrigger(
                new IllegalStateException("Invalid API key")));
    }
}
