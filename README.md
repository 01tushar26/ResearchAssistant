# Code-Buddy — Backend

A Spring Boot REST API that powers the Code-Buddy Chrome extension. It receives selected code, builds a structured prompt, and returns an AI-generated response from either a local Ollama model or HuggingFace cloud inference.

---

## Tech Stack

| | |
|---|---|
| Java 17+ | Core language |
| Spring Boot 3 | REST API framework |
| Spring Profiles | Dev/prod provider switching |
| RestClient | Outbound HTTP to AI providers |
| Lombok | Boilerplate reduction |
| Ollama | Local LLM for development |
| HuggingFace Inference API | Cloud LLM for production |

---

## Project Structure

```
src/main/java/com/buddy/Code_Buddy/
├── Controller/
│   └── ResearchController.java          # POST /research/process
├── Clients/
│   ├── AiService.java                   # Interface: generate()
│   ├── OlamaAiServiceImpl.java          # Active on dev profile
│   ├── HuggingFaceAiServiceImpl.java    # Active on prod profile
│   └── PromptBuilder.java               # Builds prompts per operation
├── DTO/
│   ├── ResearchRequest.java             # content + operation
│   ├── OlamaModelRequest/Response.java
│   └── HuggingFaceRequest/Response.java
├── Advice/
│   ├── ApiError.java
│   ├── ApiResponse.java
│   └── GlobalExceptionHandler.java
└── Configurations/
    └── RestConfig.java                  # RestClient bean
```

---

## Getting Started

### Prerequisites

- Java 17+, Maven
- [Ollama](https://ollama.com/) for dev, or a HuggingFace token for prod

### Setup

```bash
git clone https://github.com/your-username/code-buddy.git
cd code-buddy

# Dev only — pull the local model
ollama pull qwen2.5-coder:3b
```

Set environment variables in `application.properties` or your shell:

```properties
BASE_URL=http://localhost:11434            # dev (Ollama)
# BASE_URL=https://router.huggingface.co  # prod (HuggingFace)
HF_TOKEN=your_token_here                  # prod only
```

Start the server:

```bash
# Dev — local Ollama
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Prod — HuggingFace
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Server runs at `http://localhost:8080`.

---

## API

### `POST /research/process`

**Request**
```json
{
  "content": "public String generate(...) { ... }",
  "operation": "EXPLAIN"
}
```

`operation` accepts: `SUMMARIZE`, `EXPLAIN`, `REFACTOR`

**Success** — `200 OK`, `Content-Type: text/plain`, markdown-formatted response body.

**Error**
```json
{
  "error": {
    "message": "Content is empty",
    "status": "INTERNAL_SERVER_ERROR",
    "timestamp": "2024-06-01T10:30:00Z"
  }
}
```

---

## AI Providers

| | Dev (Ollama) | Prod (HuggingFace) |
|---|---|---|
| Model | `qwen2.5-coder:3b` | `Qwen/Qwen3.5-397B-A17B` |
| Endpoint | `POST /api/generate` | `/v1/chat/completions` |
| Data | Stays local | Sent to cloud |
| Streaming | Disabled | Temperature `0.3`, max 800 tokens |

Spring Profiles handle the switch — no code changes needed between environments.

---

## Configuration

| Variable | Description | Required |
|---|---|---|
| `BASE_URL` | AI provider base URL | Always |
| `HF_TOKEN` | HuggingFace Bearer token | Prod only |
| `spring.profiles.active` | `dev` or `prod` | Always |

---

## Error Handling

All exceptions flow through `GlobalExceptionHandler` and return a consistent error shape.

| Situation | Status |
|---|---|
| Empty or null AI response | 404 |
| Missing content or operation | 500 |
| Unknown operation value | 500 |
| Unexpected failure | 500 |

### [Link for Extension(Chrome) Repository](https://github.com/01tushar26/ResearchAssistant-Extension)