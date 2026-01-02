package com.securecode.backend;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorStoreService {

    private final Map<PolicyChunk, float[]> index = new HashMap<>();

    public void addPolicyEmbedding(PolicyChunk chunk, float[] embedding) {
        index.put(chunk, embedding);
    }

    public List<RetrievalResult> search(float[] queryVector, int topK) {
        List<RetrievalResult> results = new ArrayList<>();

        for (Map.Entry<PolicyChunk, float[]> entry : index.entrySet()) {
            double score = cosineSimilarity(queryVector, entry.getValue());
            results.add(new RetrievalResult(entry.getKey(), score));
        }

        List<RetrievalResult> sortedResults = results.stream()
                .sorted((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()))
                .limit(topK)
                .collect(Collectors.toList());

        System.out.println("Search Results:");
        for (RetrievalResult r : sortedResults) {
            System.out.println("- Score: " + r.getSimilarityScore() + ", Category: " + r.getChunk().getCategory());
        }

        return sortedResults;
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        if (normA == 0 || normB == 0)
            return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static class RetrievalResult {
        private final PolicyChunk chunk;
        private final double similarityScore;

        public RetrievalResult(PolicyChunk chunk, double similarityScore) {
            this.chunk = chunk;
            this.similarityScore = similarityScore;
        }

        public PolicyChunk getChunk() {
            return chunk;
        }

        public double getSimilarityScore() {
            return similarityScore;
        }
    }
}
