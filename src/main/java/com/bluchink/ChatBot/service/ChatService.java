package com.bluchink.ChatBot.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {

    private final WebClient webClient;

    @Value("${dify.api.url}")
    private String difyApiUrl;

    @Value("${dify.api.key}")
    private String apiKey;

    public ChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://34.47.137.233").build();
    }

    public Mono<String> getChatbotResponse(String userMessage) {
        Map<String, Object> chatbotRequest = new HashMap<>();
        chatbotRequest.put("inputs", new HashMap<>());
        chatbotRequest.put("query", userMessage);
        chatbotRequest.put("response_mode", "blocking");
        chatbotRequest.put("conversation_id", "");
        chatbotRequest.put("user", "abc-123");
        chatbotRequest.put("files", new Object[]{});

        return webClient.post()
                .uri("/v1/chat-messages")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chatbotRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("answer"));
    }
}
