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
                Provide a structured and concise summary of the following content.

                Your response must include:
                1. Core Purpose
                2. Key Components or Logic
                3. Intended Outcome or Impact

              
                Give Results in bulleted points.
                Avoid repetition.
                Do not restate the entire content.
    
                Content:
                """);

            case EXPLAIN -> prompt.append("""
    You are a senior software engineer.
    Explain the following code with depth.

    Include:
    - Explain line by line
    - What the code does
    - Why it exists (business purpose)
    - Where it would typically be used
    - Important logic decisions
    - Possible edge cases
    - Performance or design concerns

    Provide a structured answer.

    Code:
    """);

            case REFACTOR -> prompt.append("""
    Review the following code carefully.

    Provide:
    - Code quality assessment
    - Readability improvements
    - Performance optimizations
    - Architectural improvements
    - Best practice recommendations
    - Use Case example with code

    Keep suggestions practical and implementable.

    Code:
    """);
            default -> throw new IllegalArgumentException("Unknown Operation :" + request.getOperation());

        }
        prompt.append("\n\n");
        prompt.append(request.getContent());
        return prompt.toString();
    }
}
