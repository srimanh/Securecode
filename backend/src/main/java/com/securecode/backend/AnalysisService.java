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

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.model:gpt-4o-mini}")
    private String modelName;

    @Value("${ai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
            You are SecureCode, a Developer Educator & Security Communicator.
            Your job is to explain risk without panic, and fixes without arrogance.
            You must ONLY answer using the provided security policy context.
            If the context does not explicitly cover the question or code, respond exactly with:
            "Not covered by provided security guidelines."

            Mandatory Response Structure (JSON):
            1. issue: "What's the Issue" (e.g., "This code may allow SQL Injection.")
            2. explanation: "Why It's Risky" (e.g., "User input is directly added to the query without validation.")
            3. exploit: "How It Can Be Exploited" (e.g., "An attacker could modify the query to access unauthorized data.")
            4. safeAlternative: "Safer Alternative" (e.g., "Use prepared statements with parameter binding.")

            Additional Fields:
            - risk: A brief summary of the impact.
            - severity: Map categories to severity: (SQL Injection -> HIGH, Hardcoded Secrets -> HIGH, Input Validation -> MEDIUM, others -> LOW).
            - isSecure: boolean.
            - policyCategory: The category from the context.

            Mandatory Language Rules:
            - NEVER say "guaranteed", "100% safe", "completely secure", or "fully secure".
            - ALWAYS use conditional language: "may", "suggests", "might", "is typically associated with".
            - Do NOT speculate or invent security rules.
            - Explain risks clearly and conservatively like a mentor talking.
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
                "model", modelName,
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
            long startTime = System.currentTimeMillis();
            Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);
            long duration = System.currentTimeMillis() - startTime;

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            if (content.contains("Not covered by provided security guidelines")) {
                System.out.println("[SECURECODE] AI Refusal received in context.");
                return AnalysisResult.refusal();
            }

            System.out.println("[SECURECODE] AI call successful (" + duration + "ms)");
            return objectMapper.readValue(content, AnalysisResult.class);
        } catch (Exception e) {
            System.err.println("[SECURECODE] Critical AI API Error: " + e.getMessage());
            throw new RuntimeException("AI_API_FAILURE");
        }
    }
}
