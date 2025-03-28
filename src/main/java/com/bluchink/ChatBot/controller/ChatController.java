package com.bluchink.ChatBot.controller;

import com.bluchink.ChatBot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://192.168.45.94:3000")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public Mono<ResponseEntity<Map<String, String>>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        return chatService.getChatbotResponse(userMessage)
                .map(response -> ResponseEntity.ok(Map.of("response", response)));
    }
}
