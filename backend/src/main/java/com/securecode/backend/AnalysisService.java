package com.securecode.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {

    @Value("${openai.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

    private static final String SYSTEM_PROMPT = """
            You are SecureCode, an AI security assistant.
            You must ONLY answer using the provided security policy context.
            If the context does not explicitly cover the question or code, respond exactly with:
            "Not covered by provided security guidelines."

            Rules:
            - Do NOT speculate
            - Do NOT invent security rules
            - Do NOT guarantee safety
            - Explain risks clearly and conservatively
            - Reference the policy category in your explanation
            - Respond ONLY in valid JSON format matching the schema provided.
            """;

    public AnalysisResult analyze(String query, List<PolicyChunk> context) {
        if (apiKey == null || apiKey.isEmpty()) {
            return AnalysisResult.refusal();
        }

        StringBuilder contextBuilder = new StringBuilder("SECURITY POLICIES:\\n");
        for (PolicyChunk chunk : context) {
            contextBuilder.append("- Category: ").append(chunk.getCategory())
                    .append("\\nText: ").append(chunk.getText()).append("\\n\\n");
        }

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content",
                                "CONTEXT:\\n" + contextBuilder.toString() + "\\n\\nUSER QUERY: " + query)),
                "response_format", Map.of("type", "json_object"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(CHAT_URL, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            if (content.contains("Not covered by provided security guidelines")) {
                return AnalysisResult.refusal();
            }

            return objectMapper.readValue(content, AnalysisResult.class);
        } catch (Exception e) {
            System.err.println("Error calling OpenAI Chat: " + e.getMessage());
            return AnalysisResult.refusal();
        }
    }
}
