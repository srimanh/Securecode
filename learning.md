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

## Hour 7 — Frontend & Backend Integration

- Connected React frontend to SecureCode analysis API
- Learned to manage async UI states for AI responses
- Handled refusal and error cases gracefully in the UI
- Understood how UX impacts trust in AI security tools

## Hour 8 — Demo Optimization & Example Scenarios

- Added predefined insecure code examples for faster demos
- Learned how product UX impacts hackathon judging
- Improved clarity by showing secure vs insecure patterns
- Designed predictable demo flows for AI systems

## Hour 9 — Explanation Quality & Developer Education

- Learned how structured explanations improve trust in security tools
- Designed mentor-style AI responses instead of warning-only outputs
- Connected vulnerabilities to real-world impact and exploitation
- Improved refusal messaging to reinforce responsible AI behavior


## Hour 10 — Stability & Production Polish

- Implemented backend logging without exposing sensitive data
- Added global error handling for predictable system behavior
- Improved frontend copy and UI consistency
- Learned how polish influences trust in AI tools

## Hour 11 — Documentation & System Storytelling

- Learned how clear documentation increases project credibility
- Explained AI architecture and safety decisions in simple terms
- Prepared resume-ready project descriptions
- Understood the importance of stating limitations honestly


