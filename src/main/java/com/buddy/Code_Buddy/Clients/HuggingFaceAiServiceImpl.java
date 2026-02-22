package com.buddy.Code_Buddy.Clients;

import com.buddy.Code_Buddy.DTO.*;
import com.buddy.Code_Buddy.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
@Profile("prod")
@RequiredArgsConstructor
public class HuggingFaceAiServiceImpl implements AiService {

    private final RestClient restClient;
    private final PromptBuilder promptBuilder;

    @Value("${HF_TOKEN}")
    private String apikey;

    @Override
    public String generate(ResearchRequest request) {

        log.info("Generating the response for your request {}",request);

        String prompt = promptBuilder.buildPrompt(request);

        //Making the request body correspond to the model we use
        HuggingFaceRequest huggingFaceRequest = new HuggingFaceRequest();
        huggingFaceRequest.setModel("Qwen/Qwen3.5-397B-A17B:novita");
        huggingFaceRequest.setTemperature(0.3);
        huggingFaceRequest.setMax_tokens(500);
        huggingFaceRequest.setMessages(List.of(
                new HuggingFaceRequest.Message("user", prompt)
        ));

       HuggingFaceResponse response =
                restClient.post()
                        .uri("https://router.huggingface.co/v1/chat/completions")
                        .header("Authorization", "Bearer " + apikey)
                        .header("Content-Type", "application/json")
                        .body(huggingFaceRequest)
                        .retrieve()
                        .body(HuggingFaceResponse.class);

        if (response == null
                || response.getChoices() == null
                || response.getChoices().isEmpty()
                || response.getChoices().getFirst().getMessage() == null) {

            throw new ResourceNotFoundException("Invalid Hugging Face response");
        }

        return response.getChoices().getFirst().getMessage().getContent();
    }

}
