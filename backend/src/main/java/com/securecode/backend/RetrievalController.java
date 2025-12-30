package com.securecode.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/retrieval")
@CrossOrigin(origins = "*") // For local dev
public class RetrievalController {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private VectorStoreService vectorStoreService;

    @PostMapping("/search")
    public List<VectorStoreService.RetrievalResult> search(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.isEmpty()) {
            return List.of();
        }

        float[] queryVector = embeddingService.getEmbedding(query);
        return vectorStoreService.search(queryVector, 5);
    }
}
