package com.bluchink.ChatBot.service;


import com.bluchink.ChatBot.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${dify.api.url}")
    private String difyApiUrl;

    @Value("${dify.api.key}")
    private String apiKey;

    private final CacheManager cacheManager;


    public ChatService(WebClient.Builder webClientBuilder, CacheManager cacheManager) {
        this.webClient = webClientBuilder.baseUrl("http://34.47.137.233").build();
        this.cacheManager = cacheManager;
    }

    @CachePut(value = "conversationCache", key = "#userId")
    public void saveConversationId(String userId, String conversationId) {
        cacheManager.getCache("conversationCache").put(userId, conversationId);
    }

    @Cacheable(value = "conversationCache", key = "#userId")
    public String getConversationId(String userId) {
        String conversationId = cacheManager.getCache("conversationCache").get(userId, String.class);
        return conversationId;
    }


    public Mono<ChatResponse> getChatbotResponse(String query, String conversation_id) {
        Map<String, Object> chatbotRequest = new HashMap<>();
        chatbotRequest.put("inputs", new HashMap<>());
        chatbotRequest.put("query", query);
        chatbotRequest.put("response_mode", "blocking");
        chatbotRequest.put("conversation_id", conversation_id);
        chatbotRequest.put("user", "abc-123");
        chatbotRequest.put("files", new Object[]{});

        return webClient.post()
                .uri("/v1/chat-messages")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chatbotRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (ChatResponse) new ChatResponse((String) response.get("answer"), (String) response.get("conversation_id")));
    }

    public Flux<ChatResponse> streamChatbotResponse(String query) {

        String conversationId = getConversationId("abc-123");
        if (conversationId == null) {
            conversationId = "";
        }
        System.out.println("Conversation_Id: "+conversationId);

        Map<String, Object> chatbotRequest = new HashMap<>();
        chatbotRequest.put("inputs", new HashMap<>());
        chatbotRequest.put("query", query);
        chatbotRequest.put("response_mode", "streaming");
        chatbotRequest.put("conversation_id", conversationId);
        chatbotRequest.put("user", "abc-123");
        chatbotRequest.put("files", new Object[]{});

        return webClient.post()
                .uri("/v1/chat-messages")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chatbotRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::parseChatResponse)
                .filter(response -> response.getAnswer() != null)
                .doOnNext(response -> {
                    String newConversationId = response.getConversation_id();
                    saveConversationId("abc-123", newConversationId);
                });
    }

    private ChatResponse parseChatResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("answer") && jsonNode.has("conversation_id")) {
                return new ChatResponse(jsonNode.get("answer").asText(), jsonNode.get("conversation_id").asText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ChatResponse(null, null);
    }

   
}


