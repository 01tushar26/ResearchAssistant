# Code-Buddy

Code-Buddy is a developer tool that cuts out the friction of analyzing unfamiliar code. Instead of switching tabs, opening a chat window, and pasting snippets back and forth, you simply select text on any webpage and get an AI-generated response right in your browser's side panel — summarized, explained, or refactored, depending on what you need.

The backend is a Spring Boot application that can run against a fully local Ollama model during development, or switch to HuggingFace's cloud inference in production. The frontend is a Chrome extension using the Side Panel API introduced in Manifest V3.

---

## Table of Contents

- [What It Does](#what-it-does)
- [How It Works](#how-it-works)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Backend Setup](#backend-setup)
    - [Chrome Extension Setup](#chrome-extension-setup)
- [Chrome Extension Files](#chrome-extension-files)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [AI Providers](#ai-providers)
- [Using the Extension](#using-the-extension)
- [Error Handling](#error-handling)
- [Contributing](#contributing)

---

## What It Does

Code-Buddy supports three operations on any selected text.

**Summarize** — Produces a short, structured breakdown covering what the code does, its key components, and its intended outcome. Useful when you land on an unfamiliar file and need the gist quickly.

**Explain** — Goes deeper. It analyzes what the code does, why it exists, the decisions made in the logic, potential failure scenarios, and any design concerns around performance or maintainability. Aimed at code review situations.

**Refactor** — Identifies concrete quality issues in the code and rewrites it with improvements. The output includes both the list of problems found and the corrected version.

There is also a Notes panel in the extension that persists your thoughts locally across sessions using Chrome's storage API — handy for keeping observations while reading through a codebase.

---

## How It Works

The Chrome extension captures whatever text you have selected on the active tab. It sends that text along with the chosen operation to the Spring Boot backend at `localhost:8080`. The backend constructs a structured prompt tailored to the operation, forwards it to the configured AI provider, and returns the plain-text response. The extension then renders the markdown-formatted response directly in the side panel.

```
Chrome Extension
      |
      |  POST /research/process
      |  { content, operation }
      v
Spring Boot Backend
      |
      |-- PromptBuilder constructs a structured prompt
      |
      |-- AiService routes to the active profile
      |       |
      |       |-- dev:  Ollama  (local, qwen2.5-coder:3b)
      |       |-- prod: HuggingFace (Qwen/Qwen3.5-397B-A17B)
      |
      |  returns plain text
      v
Chrome Extension renders the response
```

Spring Profiles handle the provider switch cleanly. `OlamaAiServiceImpl` is active under `dev`, `HuggingFaceAiServiceImpl` under `prod`. No code changes are needed when moving between environments.

---

## Tech Stack

### Backend

| | |
|---|---|
| Java 17+ | Core language |
| Spring Boot 3 | REST API and application framework |
| Spring Profiles | Environment-based provider switching |
| RestClient | HTTP client for outbound AI API calls |
| Lombok | Reduces boilerplate on DTOs and services |
| Ollama | Local LLM runtime for development |
| HuggingFace Inference API | Cloud-hosted LLM for production |

### Chrome Extension

| | |
|---|---|
| Manifest V3 | Current Chrome extension standard |
| Vanilla JavaScript | Side panel logic and API interaction |
| Chrome Side Panel API | Renders the extension as a persistent panel |
| Chrome Storage API | Saves notes locally across sessions |
| Chrome Scripting API | Reads the user's text selection from the active tab |

---

## Project Structure

```
Code-Buddy/
│
└── src/main/java/com/buddy/Code_Buddy/
   │
   ├── Controller/
   │   └── ResearchController.java          # Exposes POST /research/process
   │
   ├── Clients/
   │   ├── AiService.java                   # Interface with a single generate() method
   │   ├── OlamaAiServiceImpl.java          # Ollama implementation, active on dev profile
   │   ├── HuggingFaceAiServiceImpl.java    # HuggingFace implementation, active on prod profile
   │   └── PromptBuilder.java               # Builds the prompt string based on operation
   │
   ├── DTO/
   │   ├── ResearchRequest.java             # Inbound payload: content + operation enum
   │   ├── OlamaModelRequest.java           # Request shape for Ollama's /api/generate
   │   ├── OlamaModelResponse.java          # Response shape from Ollama
   │   ├── HuggingFaceRequest.java          # Request shape for HuggingFace chat completions
   │   └── HuggingFaceResponse.java         # Response shape from HuggingFace
   │
   ├── Advice/
   │   ├── ApiError.java                    # Error detail model
   │   ├── ApiResponse.java                 # Generic response wrapper used across endpoints
   │   └── GlobalExceptionHandler.java      # Catches and formats all exceptions consistently
   │
   └── Configurations/
       └── RestConfig.java                  # Declares the RestClient bean with a base URL


```

---
### [Link for Extension Repo](https://github.com/01tushar26/ResearchAssistant-Extension)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- A Chromium-based browser (Chrome, Edge, Brave)
- [Ollama](https://ollama.com/) installed and running locally, if using the dev profile
- A HuggingFace account and API token, if using the prod profile

### Backend Setup

Clone the repository and navigate to the project root.

```bash
git clone https://github.com/your-username/code-buddy.git
cd code-buddy
```

If you plan to run in development mode, pull the local model first.

```bash
ollama pull qwen2.5-coder:3b
```

Set the required environment variables. You can add these to your `application.properties` or export them in your shell before starting the server.

```properties
# Used by both profiles
BASE_URL=http://localhost:11434            # For dev (Ollama)
# BASE_URL=https://router.huggingface.co  # For prod (HuggingFace)

# Required only for prod
HF_TOKEN=your_huggingface_api_token
```

Start the backend with the appropriate profile.

```bash
# Local development using Ollama
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production using HuggingFace
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

The server will be available at `http://localhost:8080`.

### Chrome Extension Setup

1. Open Chrome and go to `chrome://extensions/`
2. Enable **Developer Mode** using the toggle in the top-right corner
3. Click **Load unpacked** and select the `chrome-extension/` folder from this repository
4. Pin the extension to your toolbar for quick access

---

## Chrome Extension Files

The extension is built with five files, each with a distinct responsibility.

### `manifest.json`

The extension's configuration file, required by Chrome. It declares the extension's name, version, and Manifest V3 compliance. It also specifies the permissions the extension needs — `activeTab` and `scripting` to read the user's text selection, `storage` to persist notes, and `sidePanel` to render the UI as a side panel. The `host_permissions` entry grants access to `localhost:8080` so the extension can reach the Spring Boot backend, as well as `<all_urls>` so it can capture selected text on any webpage.

### `background.js`

A Manifest V3 service worker that runs in the background. Its only job is to call `chrome.sidePanel.setPanelBehavior({ openPanelOnActionClick: true })`, which tells Chrome to open the side panel automatically whenever the user clicks the extension's toolbar icon. Without this, the panel would not open on click.

### `sidePanel.html`

The markup file that defines the extension's visible UI. It contains three main sections: an **Actions** row with a dropdown to select the operation (Summarize, Explain, Refactor) and a Process button; a **Notes** section with a textarea and a Save button; and a **Results** div where the AI response is rendered. It loads `sidePanel.css` for styling and `sidePanel.js` for behavior.

### `sidePanel.js`

The core logic of the extension. On load it restores any previously saved notes from Chrome's local storage. When the user clicks **Process**, it queries the active tab, uses the Scripting API to extract the currently selected text, and POSTs it along with the chosen operation to `http://localhost:8080/research/process`. The response is then passed through a lightweight markdown-to-HTML formatter (`showResult`) that converts headings and bold syntax before rendering it in the results div. The Save Notes button writes the textarea content to `chrome.storage.local` for persistence across sessions. All loading state (spinner, button disable, text change) is managed here as well.

### `sidePanel.css`

Styles for the side panel UI. It sets a clean card-based layout with a white panel on a light grey background, using Segoe UI as the font. It styles the dropdown, Process button, textarea, and results area with rounded corners, focus highlights in blue (`#1a73e8`), and subtle box shadows. It also defines the CSS spinner animation used during loading, and a `.hidden` utility class used by `sidePanel.js` to toggle the spinner's visibility.

---

## Configuration

| Variable | Description | Required |
|---|---|---|
| `BASE_URL` | Base URL of the AI provider (Ollama or HuggingFace) | Always |
| `HF_TOKEN` | HuggingFace API Bearer token | Prod only |
| `spring.profiles.active` | Set to `dev` or `prod` | Always |

---

## API Reference

### POST /research/process

Accepts selected text and an operation, returns a formatted AI-generated response as plain text.

**Request body**

```json
{
  "content": "public String generate(ResearchRequest request) { ... }",
  "operation": "EXPLAIN"
}
```

The `operation` field accepts one of three values: `SUMMARIZE`, `EXPLAIN`, or `REFACTOR`.

**Success response**

```
200 OK
Content-Type: text/plain

### What It Does
- Accepts a ResearchRequest and delegates prompt construction to PromptBuilder...
```

**Error response**

```json
{
  "error": {
    "message": "Content is empty .. Please select the content",
    "status": "INTERNAL_SERVER_ERROR",
    "timestamp": "2024-06-01T10:30:00.000+00:00"
  },
  "time": "2024-06-01T10:30:00"
}
```

---

## AI Providers

### Ollama — Local (dev profile)

Ollama runs entirely on your machine. No data leaves your environment, which makes it the right choice during development or when working with sensitive or proprietary code.

- Model: `qwen2.5-coder:3b`
- Endpoint: `POST /api/generate`
- Streaming is disabled so the full response is returned at once

### HuggingFace — Cloud (prod profile)

HuggingFace is used in production via their inference router, which routes to a hosted version of a significantly larger model for better output quality.

- Model: `Qwen/Qwen3.5-397B-A17B` via Novita
- Endpoint: `https://router.huggingface.co/v1/chat/completions`
- Temperature: `0.3` — kept low for consistent, deterministic output
- Max tokens: `800`

---

## Using the Extension

1. Open any webpage that contains code — a GitHub file, a Stack Overflow answer, documentation, anything.
2. Select the code or text you want to analyze.
3. Click the Code-Buddy icon in your toolbar. The side panel opens on the right side of your browser.
4. Choose an operation from the dropdown: Summarize, Explain, or Refactor.
5. Click **Process**. The button shows a loading spinner while the request is in flight.
6. The response appears below, formatted with headings and bullet points pulled directly from the AI output.

If you want to take notes while reading, use the Notes section at the bottom of the panel. Whatever you write there is saved locally and will still be there the next time you open the extension.

---

## Error Handling

All exceptions are caught centrally by `GlobalExceptionHandler` and returned in a consistent shape so the extension always has something meaningful to display.

| Situation | HTTP Status |
|---|---|
| AI provider returned an empty or null response | 404 Not Found |
| Request was sent without content or an operation | 500 Internal Server Error |
| An unrecognized operation value was passed | 500 Internal Server Error |
| Any other unexpected failure | 500 Internal Server Error |

On the extension side, errors surface inline in the results area with a short description of what went wrong.

---

## Contributing

Fork the repository, create a branch off `main`, and open a pull request with a clear description of what you changed and why. Please make sure the backend compiles cleanly and the extension loads without errors before submitting.

---
