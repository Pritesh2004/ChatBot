package com.bluchink.ChatBot.service;
import com.bluchink.ChatBot.dto.ChatRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {

    private final WebClient webClient;

    public ChatService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String sendMessage(String query, String user) {
        Map<String, Object> inputs = new HashMap<>();
        ChatRequest request = new ChatRequest(inputs, query, "blocking", "", user, Collections.emptyList());

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(10));
    }
}
