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

    private static final double SIMILARITY_THRESHOLD = 0.75;
    private static final int MAX_INPUT_LENGTH = 5000;

    public AnalyzeResponse analyze(AnalyzeRequest request) {
        String input = request.getInput();

        // 1. Input Validation
        if (input == null || input.trim().isEmpty()) {
            return AnalyzeResponse.refusal("Input cannot be empty.");
        }
        if (input.length() > MAX_INPUT_LENGTH) {
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
            return AnalyzeResponse.refusal(
                    "Not covered by provided security guidelines. Lower similarity than " + SIMILARITY_THRESHOLD);
        }

        // 5. Call safety-constrained analysis
        AnalysisResult result = analysisService.analyze(input, filteredPolicies);

        // 6. Map to response with Language Sentinel
        AnalyzeResponse response = new AnalyzeResponse();
        if (result.getIssue().equals("Refused")) {
            response.setMessage(result.getExplanation());
        } else {
            response.setIsSecure(result.isSecure());
            response.setIssue(result.getIssue());
            response.setExplanation(result.getExplanation());
            response.setPolicyCategory(result.getPolicyCategory());
            response.setSafeAlternative(result.getSafeAlternative());

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
