package com.bluchink.ChatBot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class ChatResponse {

    private String answer;
    private String conversation_id;

    public ChatResponse(String answer, String conversation_id) {
        this.answer = answer;
        this.conversation_id = conversation_id;
    }

    public ChatResponse() {
    }
}
