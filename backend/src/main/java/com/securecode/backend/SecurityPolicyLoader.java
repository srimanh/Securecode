package com.securecode.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class SecurityPolicyLoader {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private VectorStoreService vectorStoreService;

    @PostConstruct
    public void init() {
        loadAndChunkPolicies();
    }

    public List<PolicyChunk> loadAndChunkPolicies() {
        List<PolicyChunk> chunks = new ArrayList<>();

        // Load documents
        String[] files = { "owasp-top-10.txt", "owasp-injection.txt", "owasp-cryptographic-failures.txt" };

        for (String file : files) {
            try {
                String content = readFile(file);
                List<String> rawChunks = chunkText(content);
                for (String chunk : rawChunks) {
                    PolicyChunk policyChunk = new PolicyChunk();
                    policyChunk.setText(chunk);
                    policyChunk.setSource("OWASP");
                    policyChunk.setCategory(assignCategory(chunk));
                    policyChunk.setSeverity(assignSeverity(chunk));

                    // Generate embedding and store in VectorStore
                    float[] embedding = embeddingService.getEmbedding(chunk);
                    vectorStoreService.addPolicyEmbedding(policyChunk, embedding);

                    chunks.add(policyChunk);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return chunks;
    }

    private String readFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("security-docs/" + fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] sections = text.split("\n\n"); // Split by double newlines (paragraphs)
        StringBuilder currentChunk = new StringBuilder();

        for (String section : sections) {
            if (currentChunk.length() + section.length() > 2000) { // Rough word limit
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
            }
            currentChunk.append(section).append("\n\n");
        }
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    private String assignCategory(String text) {
        if (text.toLowerCase().contains("sql")) {
            return "SQL_INJECTION";
        } else if (text.toLowerCase().contains("password") || text.toLowerCase().contains("api key")
                || text.toLowerCase().contains("hardcoded")) {
            return "SECRETS";
        } else if (text.toLowerCase().contains("input") || text.toLowerCase().contains("validation")) {
            return "INPUT_VALIDATION";
        } else {
            return "GENERAL";
        }
    }

    private String assignSeverity(String text) {
        // Simple logic: HIGH if contains certain keywords
        if (text.toLowerCase().contains("vulnerable") || text.toLowerCase().contains("attack")) {
            return "HIGH";
        } else {
            return "MEDIUM";
        }
    }
}