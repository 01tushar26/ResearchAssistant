package com.buddy.Code_Buddy.Configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {
    @Value("${BASE_URL}")
    String baseURL;
    @Bean
    public RestClient getRestClient(){
        return RestClient.builder()
                .baseUrl(baseURL)
                .build();

    }
}
