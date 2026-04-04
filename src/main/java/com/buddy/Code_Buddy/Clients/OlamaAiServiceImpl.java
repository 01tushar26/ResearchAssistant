package com.buddy.Code_Buddy.Clients;

import com.buddy.Code_Buddy.DTO.OlamaModelRequest;
import com.buddy.Code_Buddy.DTO.OlamaModelResponse;
import com.buddy.Code_Buddy.DTO.ResearchRequest;
import com.buddy.Code_Buddy.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class OlamaAiServiceImpl implements AiService{

    private final RestClient restClient;
    private final PromptBuilder promptBuilder;

    @Override
    public String generate(ResearchRequest request) {

        log.info("Generating the response for your request {}",request);

        String prompt = promptBuilder.buildPrompt(request);

        //Making the request body correspond to the model we use
        OlamaModelRequest olamaModelRequest = new OlamaModelRequest(
                "qwen2.5-coder:3b",
                prompt,
                false
        );

        //Generating the response

        OlamaModelResponse response = restClient.post()
                .uri("/api/generate")
                .body(olamaModelRequest)
                .retrieve()
                .body(OlamaModelResponse.class);

        if(response == null || response.getResponse() == null){
            throw  new ResourceNotFoundException("Ollama Response is empty");
        }



        return response.getResponse();
    }


    }
