package com.securecode.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecureCodeService {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private VectorStoreService vectorStoreService;

    @Autowired
    private AnalysisService analysisService;

    private static final double SIMILARITY_THRESHOLD = 0.7;
    private static final int MAX_INPUT_LENGTH = 5000;

    private static final String MOCK_SQL = "String query = \"SELECT * FROM users WHERE id = \" + userId;";
    private static final String MOCK_SECRET = "String apiKey = \"sk_test_123456\";";
    private static final String MOCK_VALIDATION = "int age = Integer.parseInt(request.getParameter(\"age\"));";

    public AnalyzeResponse analyze(AnalyzeRequest request) {
        String input = request.getInput();
        System.out.println("[SECURECODE] Analysis request received. Length: " + (input != null ? input.length() : 0));

        // 1. Input Validation
        if (input == null || input.trim().isEmpty()) {
            return AnalyzeResponse.refusal("Input cannot be empty.");
        }

        // 1b. Mock Demo Logic (Hour 10)
        if (input.contains(MOCK_SQL)) {
            System.out.println("[SECURECODE] Serving Mock Response: SQL Injection");
            return new AnalyzeResponse(false, "SQL Injection", "User input is directly concatenated into a SQL string.",
                    "Injection", "Use PreparedStatement with parameter binding.", "Serious security risk detected.",
                    "Attacker could steal or delete data.", "HIGH", null);
        }
        if (input.contains(MOCK_SECRET)) {
            System.out.println("[SECURECODE] Serving Mock Response: Hardcoded Secret");
            return new AnalyzeResponse(false, "Hardcoded Secret", "Sensitive credentials found in source code.",
                    "Secrets", "Use environment variables or a Secret Manager.", "Credential leak risk.",
                    "Attacker could gain unauthorized API access.", "HIGH", null);
        }
        if (input.contains(MOCK_VALIDATION)) {
            System.out.println("[SECURECODE] Serving Mock Response: Missing Validation");
            return new AnalyzeResponse(false, "Insecure Parsing",
                    "Raw request data parsed without range or type validation.", "Input Validation",
                    "Implement strict schema validation and range checks.", "Data integrity risk.",
                    "Potential DoS or logic bypass via crafted inputs.", "MEDIUM", null);
        }

        if (input.length() > MAX_INPUT_LENGTH) {
            System.out.println("[SECURECODE] Rejection: Input too long");
            return AnalyzeResponse
                    .refusal("Input is too long (max 5000 chars). Security tools must defend themselves too.");
        }

        // 2. Generate embedding
        float[] queryVector = embeddingService.getEmbedding(input);

        // 3. Retrieve relevant policies
        List<VectorStoreService.RetrievalResult> allResults = vectorStoreService.search(queryVector, 5);

        // 4. Apply Hard Similarity Gate
        List<PolicyChunk> filteredPolicies = allResults.stream()
                .filter(r -> r.getSimilarityScore() >= SIMILARITY_THRESHOLD)
                .map(VectorStoreService.RetrievalResult::getChunk)
                .collect(Collectors.toList());

        if (filteredPolicies.isEmpty()) {
            System.out.println("[SECURECODE] Refusal: Low similarity context");
            return AnalyzeResponse.refusal(
                    "This input is not covered by the current security guidelines. SecureCode avoids speculative advice to ensure accuracy.");
        }

        // 5. Call safety-constrained analysis
        System.out.println("[SECURECODE] Calling AI Analysis service...");
        AnalysisResult result = analysisService.analyze(input, filteredPolicies);

        // 6. Map to response with Language Sentinel
        AnalyzeResponse response = new AnalyzeResponse();
        if (result.getIssue().equals("Refused")) {
            System.out.println("[SECURECODE] AI Refused response");
            response.setMessage(result.getExplanation());
        } else {
            System.out.println("[SECURECODE] AI Analysis successful for category: " + result.getPolicyCategory());
            response.setIsSecure(result.isSecure());
            response.setIssue(result.getIssue());
            response.setExplanation(result.getExplanation());
            response.setPolicyCategory(result.getPolicyCategory());
            response.setSafeAlternative(result.getSafeAlternative());
            response.setRisk(result.getRisk());
            response.setExploit(result.getExploit());
            response.setSeverity(result.getSeverity());

            languageSentinel(response);
        }

        return response;
    }

    private void languageSentinel(AnalyzeResponse response) {
        String[] forbiddenWords = { "guaranteed", "100% safe", "completely secure", "fully secure", "no risk" };
        String explanation = response.getExplanation().toLowerCase();

        for (String word : forbiddenWords) {
            if (explanation.contains(word)) {
                response.setExplanation(response.getExplanation().replace(word, "[CAUTION: POTENTIAL OVERCONFIDENCE]"));
                response.setMessage(
                        "Warning: AI output contained overly confident language which has been redacted for safety.");
            }
        }
    }
}
