package com.securecode.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private VectorStoreService vectorStoreService;

    @Autowired
    private AnalysisService analysisService;

    private static final double SIMILARITY_THRESHOLD = 0.70;

    @PostMapping("/analyze")
    public AnalysisResult analyze(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.isEmpty()) {
            return AnalysisResult.refusal();
        }

        // Step 1: Embed query
        float[] queryVector = embeddingService.getEmbedding(query);

        // Step 2: Retrieve relevant policies
        List<VectorStoreService.RetrievalResult> allResults = vectorStoreService.search(queryVector, 5);

        // Step 3: Hard Refusal Check
        List<PolicyChunk> filteredPolicies = allResults.stream()
                .filter(r -> r.getSimilarityScore() >= SIMILARITY_THRESHOLD)
                .map(VectorStoreService.RetrievalResult::getChunk)
                .collect(Collectors.toList());

        if (filteredPolicies.isEmpty()) {
            return AnalysisResult.refusal();
        }

        // Step 4: Perform LLM Analysis with context
        return analysisService.analyze(query, filteredPolicies);
    }
}
