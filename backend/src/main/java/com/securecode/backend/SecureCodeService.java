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

    private static final double SIMILARITY_THRESHOLD = 0.70;

    public AnalyzeResponse analyze(AnalyzeRequest request) {
        String input = request.getInput();
        if (input == null || input.isEmpty()) {
            return AnalyzeResponse.refusal("Input cannot be empty.");
        }

        // 1. Generate embedding
        float[] queryVector = embeddingService.getEmbedding(input);

        // 2. Retrieve relevant policies
        List<VectorStoreService.RetrievalResult> allResults = vectorStoreService.search(queryVector, 5);

        // 3. Apply similarity threshold
        List<PolicyChunk> filteredPolicies = allResults.stream()
                .filter(r -> r.getSimilarityScore() >= SIMILARITY_THRESHOLD)
                .map(VectorStoreService.RetrievalResult::getChunk)
                .collect(Collectors.toList());

        if (filteredPolicies.isEmpty()) {
            return AnalyzeResponse.refusal("Not covered by provided security guidelines.");
        }

        // 4. Call safety-constrained analysis
        AnalysisResult result = analysisService.analyze(input, filteredPolicies);

        // 5. Map to response
        AnalyzeResponse response = new AnalyzeResponse();
        if (result.getIssue().equals("Refused")) {
            response.setMessage(result.getExplanation());
        } else {
            response.setIsSecure(result.isSecure());
            response.setIssue(result.getIssue());
            response.setExplanation(result.getExplanation());
            response.setPolicyCategory(result.getPolicyCategory());
            response.setSafeAlternative(result.getSafeAlternative());
        }

        return response;
    }
}
