package com.buddy.Code_Buddy.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class HuggingFaceResponse {
    private List<Choice> choices;

    @Setter
    @Getter
    public static class Choice {
        private Message message;

    }

    public static class Message {
        private String role;
        @Getter
        @Setter
        private String content;

    }

}
