# 🛡️ SecureCode Engine
### Enterprise-Grade Security Policy Enforcement for Code Reviews

SecureCode Engine is a production-aware AI system built to solve the **"Security Knowledge Gap"**. While generic AI assistants often hallucinate or suggest risky code, SecureCode grounds every analysis in verified **OWASP Security Policies** using a Retrieval-Augmented Generation (RAG) pipeline.

It transforms raw security findings into clear, educational reports for developers, ensuring that security advice is accurate, compliant, and mentor-like.

---

## 🚨 The Problem
Generic AI assistants are "probabilistic" — they guess what code should look like. In security, guessing is dangerous.
- **Hallucinations**: AI suggesting "completely secure" code that actually has a zero-day.
- **Speculative Advice**: Providing security tips without referencing actual compliance standards.
- **Fear-Mongering**: Jargon-heavy warnings that developers ignore.

---

## 💡 The Solution: Grounded RAG
SecureCode acts as an **AI Security Reviewer** that only speaks when it has the facts.
- **Retrieval-Augmented Generation (RAG)**: The system first "reads" your internal security docs before the AI even sees your code.
- **Hard Similarity Gate**: If the code doesn't match a known security category, the system refuses to speculate.
- **Educational Explanations**: A standardized 4-part template: "What's the issue", "Why it's risky", "How it can be exploited", and "Safer alternative".

---

## 🏗️ Architecture Flow
```text
User Input (Code Snippet)
   ↓
Embedding Generation (Semantic Vector)
   ↓
Policy Retrieval (Vector search in OWASP Docs)
   ↓
Safety & Similarity Check (Similarity >= 0.7?)
   ↓
AI Analysis (Grounding code in retrieved context)
   ↓
Structured JSON Response (Mapped to UI)
```

---

## 🧠 Key Features

- **Reasoning-Capable AI**  
  Uses **Olmo 3.1 32B Think** models to "think" through logic before providing a verdict.
  
- **4-Part Reporting Template**  
  Every finding includes: Issue Isolation, Risk Assessment, Exploit Scenario, and a Safer Alternative.

- **Severity Awareness**  
  Automatic mapping of vulnerabilities to **HIGH**, **MEDIUM**, or **LOW** severity based on policy category.

- **Safe Refusal Logic**  
  Explicitly avoids speculative advice with a trust-reinforcing refusal:  
  _"This input is not covered by current guidelines. SecureCode avoids speculative advice to ensure accuracy."_

---

## ⚠️ Limitations (Honesty Matters)

- **V1 Categorization**: Currently focused on OWASP Top 10 (Injection, Secrets, Input Validation).
- **Advisory Only**: This is an educational and advisory tool, not a replacement for a formal security audit.
- **Static Context**: The engine is as smart as the documents you provide it in `/security-docs`.

---

## 🛠️ Tech Stack

### Backend
- **Java 17 / Spring Boot 3**
- **OpenAI Embeddings**: For semantic code representation.
- **In-Memory Vector Store**: Fast, lightweight policy retrieval.

### Frontend
- **React / Vite**: Modern, responsive dashboard.
- **Vanilla CSS**: Premium dark-mode aesthetics with glassmorphism.

---

## 🚀 Future Roadmap
- [ ] Multi-file codebase analysis.
- [ ] IDE Extension (VS Code / IntelliJ).
- [ ] Automated ticket creation (Jira/GitHub) for detected vulnerabilities.

---

## 📄 License
This project is licensed under the **MIT License**.
