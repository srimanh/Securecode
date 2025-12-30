## Hour 1 — Security Scope & Ground Truth

- Learned why AI security tools must rely on closed, authoritative documents
- Understood the importance of limiting scope to avoid hallucinations
- Selected SQL Injection, Hardcoded Secrets, and Input Validation as core domains
- Set up a clear policy source directory for future RAG ingestion

## Hour 2 — Policy Parsing & Chunking

- Learned why document chunking is critical for RAG accuracy
- Implemented structured policy chunks with metadata
- Understood trade-offs between chunk size and semantic retrieval
- Prepared security documents for embedding and search

## Hour 3 — Embeddings & Semantic Retrieval

- Learned how embeddings enable semantic search over security policies
- Implemented in-memory vector indexing for RAG
- Used cosine similarity to retrieve relevant policy chunks
- Understood why keyword search fails for security questions

## Hour 4 — Secure Prompt Design & Hallucination Control

- Engineered a strict system prompt to constrain AI behavior to provided context
- Implemented structured JSON output for predictable frontend integration
- Integrated a similarity threshold in the backend to prevent hallucinations on out-of-scope queries
- Learned to use context injection for Retrieval-Augmented Generation (RAG)

## Hour 5 — Core SecureCode Analysis API

- Orchestrated retrieval, embeddings, and safety-constrained LLM analysis into a single service
- Implemented a clean, professional API contract using explicit DTOs
- Learned the importance of a service-oriented architecture for AI workflows
- Handled out-of-scope queries gracefully through orchestration logic

## Hour 6 — Safety Guardrails, Refusal Logic & Trust Hardening

- Implemented a **Hard Similarity Gate (0.75)** to ensure only high-confidence retrievals reach the LLM
- Built a **Language Sentinel** filter to redact overconfident or absolute security claims
- Learned to use "Safe by Design" principles by forcing conditional language via system prompts
- Integrated input validation to defend the tool against token bombs and malicious queries
- Understood why security tools must prioritize "refusal over guessing" to maintain user trust
