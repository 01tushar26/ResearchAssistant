package com.buddy.Code_Buddy.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {
    @Bean
    public RestClient getRestClient(){
        return RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();

    }
}
