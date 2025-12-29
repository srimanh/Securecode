package com.securecode.backend;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SecurityPolicyLoaderTest {

    @Autowired
    private SecurityPolicyLoader loader;

    @Test
    public void testLoadAndChunk() {
        List<PolicyChunk> chunks = loader.loadAndChunkPolicies();
        for (PolicyChunk chunk : chunks) {
            System.out.println(chunk);
        }
    }
}