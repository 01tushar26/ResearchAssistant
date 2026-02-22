package com.buddy.Code_Buddy.Clients;

import com.buddy.Code_Buddy.DTO.ResearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

@Component
@Slf4j
public class PromptBuilder {
    public String buildPrompt(ResearchRequest request) {
        log.info("Building prompt for request {}",request);
        if(request.getOperation() == null || request.getContent() == null){
            log.info("Prompt building is failed ");
            throw new ResourceAccessException("Content is empty .. Please select the content ");
        }
        StringBuilder prompt = new StringBuilder();

        //this is the new switch case syntax with -> where we did not need to write the break statement

        switch (request.getOperation()){
            case SUMMARIZE -> prompt.append("""
You are an expert technical analyst.

Summarize the content below strictly in this format:

### Core Purpose
- 1–2 bullet points

### Key Components
- 2–4 bullet points

### Intended Outcome
- 1–2 bullet points

Rules:
- Use only bullet points
- Maximum 8 bullet points total
- No introduction or conclusion
- No repetition
- Be precise and technical

Content:
""");

            case EXPLAIN -> prompt.append("""
You are a senior software engineer reviewing code.

Explain the code below using this exact structure:

### What It Does
- Clear technical explanation

### Why It Exists
- Business or architectural reasoning

### Key Logic Decisions
- Important conditions or patterns used

### Edge Cases
- Potential failure scenarios

### Design Concerns
- Performance, scalability, maintainability issues

Rules:
- Use short bullet points
- No generic statements
- No filler text
- Maximum 12 bullets total

Code:
""");

            case REFACTOR -> prompt.append("""
You are a code reviewer improving production code.

Provide the output in this structure:

### Code Quality Issues
- Specific problems found

### Improved Version
```language
<refactored code here>

Code:
""");
            default -> throw new IllegalArgumentException("Unknown Operation :" + request.getOperation());

        }
        prompt.append("\n\n");
        prompt.append(request.getContent());
        return prompt.toString();
    }
}
