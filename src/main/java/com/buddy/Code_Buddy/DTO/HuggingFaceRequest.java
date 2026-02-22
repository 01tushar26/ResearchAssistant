
package com.buddy.Code_Buddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuggingFaceRequest {

    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer max_tokens;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}