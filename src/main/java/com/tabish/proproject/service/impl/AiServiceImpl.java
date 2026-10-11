package com.tabish.proproject.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabish.proproject.dto.request.AiPromptRequest;
import com.tabish.proproject.dto.response.AiResponse;
import com.tabish.proproject.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    private final String apiKey;
    private final String defaultModel;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AiServiceImpl(
            @Value("${spring.ai.google.genai.api-key:}") String apiKey,
            @Value("${spring.ai.google.genai.chat.model:gemini-3.8-flash}") String defaultModel,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.defaultModel = defaultModel != null && !defaultModel.isBlank() ? defaultModel : "gemini-3.8-flash";
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    @Override
    public AiResponse generateText(AiPromptRequest promptRequest) {
        String prompt = promptRequest.getPrompt();
        String systemPrompt = promptRequest.getSystemPrompt();
        String modelToUse = resolveModel(promptRequest.getModel());

        if (apiKey.isBlank()) {
            log.warn("Gemini API key is not configured");
            return AiResponse.builder()
                    .prompt(prompt)
                    .response("Error: Google Gemini API key is missing. Please configure 'spring.ai.google.genai.api-key'.")
                    .model(modelToUse)
                    .build();
        }

        try {
            Map<String, Object> requestPayload = new HashMap<>();

            // Build system instruction if provided and not equal to Swagger placeholder "string"
            if (systemPrompt != null && !systemPrompt.isBlank() && !"string".equalsIgnoreCase(systemPrompt.trim())) {
                Map<String, Object> sysPart = Map.of("text", systemPrompt.trim());
                requestPayload.put("systemInstruction", Map.of("parts", List.of(sysPart)));
            }

            // Build contents
            Map<String, Object> userPart = Map.of("text", prompt);
            Map<String, Object> userContent = Map.of(
                    "role", "user",
                    "parts", List.of(userPart)
            );
            requestPayload.put("contents", List.of(userContent));

            String responseBody = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(modelToUse))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(String.class);

            String generatedText = extractTextFromGeminiResponse(responseBody);

            return AiResponse.builder()
                    .prompt(prompt)
                    .response(generatedText)
                    .model(modelToUse)
                    .build();

        } catch (Exception ex) {
            log.error("Error communicating with Google Gemini API: ", ex);
            return AiResponse.builder()
                    .prompt(prompt)
                    .response("Gemini API Error: " + ex.getMessage())
                    .model(modelToUse)
                    .build();
        }
    }

    private String resolveModel(String requestedModel) {
        if (requestedModel == null || requestedModel.isBlank()
                || "string".equalsIgnoreCase(requestedModel.trim())
                || "default".equalsIgnoreCase(requestedModel.trim())
                || "gemini-2.5-flash".equalsIgnoreCase(requestedModel.trim())) {
            return this.defaultModel;
        }
        return requestedModel.trim();
    }

    private String extractTextFromGeminiResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    List<String> textParts = new ArrayList<>();
                    for (JsonNode part : parts) {
                        JsonNode textNode = part.path("text");
                        if (!textNode.isMissingNode()) {
                            textParts.add(textNode.asText());
                        }
                    }
                    if (!textParts.isEmpty()) {
                        return String.join("\n", textParts);
                    }
                }
            }
            return root.toString();
        } catch (Exception e) {
            log.warn("Failed to parse Gemini JSON response, returning raw body: {}", e.getMessage());
            return json;
        }
    }
}
